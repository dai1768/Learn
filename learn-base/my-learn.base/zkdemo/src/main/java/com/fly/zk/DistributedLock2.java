package com.fly.zk;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributedLock2 implements Lock, Watcher {
	Logger logger = LoggerFactory.getLogger(getClass());
	private ZooKeeper zk;
	private CountDownLatch connlatch;
	private CountDownLatch latch;
	private String root = "/locks";
	private String resourceName = "";
	private String lockPath = "";
	private String lockNodePrifix = "sub";
	private String myNode;
	private String waitNode;
	private int sessionTimeout;

	public DistributedLock2(String connStr, int sessionTimeout,
			String resourceName) {
		this.sessionTimeout = sessionTimeout;
		this.resourceName = resourceName;
		latch = new CountDownLatch(1);
		connlatch = new CountDownLatch(1);
		try {
			lockPath = root + "/" + resourceName;
			zk = new ZooKeeper(connStr, sessionTimeout, this);
			connlatch.await();
			Stat stat = zk.exists(root, false);
			Stat resStat = zk.exists(lockPath, false);
		
			String path = "";
			if (stat == null) {
				path = zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				logger.info("creat root node : {}",path);
			}
			
			if (resStat == null) {
				path = zk.create(lockPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				logger.info("creat res node : {}",path);
			}
			
		} catch (IOException | KeeperException | InterruptedException e) {
			throw new LockException(e);
		}
	}

	@Override
	public void lock() {
		if (tryLock()) {
			System.out.println("Thread " + Thread.currentThread().getId() + " "
					+ myNode + " get lock true");
			return;

		} else {
			try {
				waitForLock(waitNode, Integer.MAX_VALUE);
			} catch (InterruptedException | KeeperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 等待锁
		}

	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		this.lock();
	}

	@Override
	public boolean tryLock() {
		try {
			myNode = zk.create(lockPath + "/" + lockNodePrifix, new byte[0],
					ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.info("create myNode:  {}",myNode);
			List<String> children = zk.getChildren(lockPath , false);
			logger.info("find children : {} ",children);

			Collections.sort(children);
			String minNode = children.get(0);
			String myLeafNode = myNode.substring((lockPath+"/").length());
			if (minNode.equals(myLeafNode)) {
				logger.info("get lock node : {}" ,minNode);
				return true;
			} else {
				waitNode = children.get(Collections.binarySearch(children,	myLeafNode) - 1);
				logger.info("myNode: {} ,waitNode : {}",myNode ,waitNode);
			}
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		try {
			if (this.tryLock()) {
				return true;
			}
			return waitForLock(waitNode, time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void unlock() {
		try {
			logger.info("unlock " + myNode);
			zk.delete(myNode, -1);
			myNode = null;
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info(event.toString());
		System.err.println(event);
		KeeperState stat = event.getState();
		if( Event.KeeperState.SyncConnected.equals(stat)){
			if( event.getType().equals(Event.EventType.None) ){
				connlatch.countDown();
				logger.info("zk connected!");
			}else if( event.getType().equals(Event.EventType.NodeDeleted)) {
				if (latch != null) {
					latch.countDown();
				}
			}
		}
	}

	private boolean waitForLock(String lower, long waitTime)
			throws InterruptedException, KeeperException {
		Stat stat = zk.exists(lockPath + "/" + lower, true);
		// 判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
		if (stat != null) {
			logger.info("waiting for " + lockPath + "/" + lower);
			this.latch = new CountDownLatch(1);
			this.latch.await(10 * waitTime, TimeUnit.MILLISECONDS);
			this.latch = null;
			logger.info("waitNode: {} had bean deleted",waitNode);
		}
		return true;
	}

	public class LockException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public LockException(String e) {
			super(e);
		}

		public LockException(Exception e) {
			super(e);
		}
	}

	
	public static void main(String[] args) {
		
		final CountDownLatch beginLatch = new CountDownLatch(1);
		int threadNum = 50;
		for(int i=0; i<threadNum; i++){
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					Logger logger = LoggerFactory.getLogger(getClass());
					String conStr = "192.168.88.8:2180";
					DistributedLock2 lock = new DistributedLock2(conStr,300000,"res01");
					lock.lock();
					try {
						beginLatch.await();
						Thread.sleep((long) (Math.random()* 600));
						logger.info(" do job!");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally{
						lock.unlock();
					}
				}
				
			});
			t.start();
		}
		
		beginLatch.countDown();
		
	}
	
}


	
