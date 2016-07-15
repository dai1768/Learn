package org.zkdemo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupMember  implements Watcher{
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private ZooKeeper zk;
	private String root = "/group";
	private String resourceName;
	private CountDownLatch connlatch;
	private String parentPath;
	private String myNode;
	private String nodePrefix = "sub";
	private volatile boolean isRunning = true;
	private volatile boolean isNeedCheck = true;
	
	public GroupMember(String conStr,int sessionTimeout,String resName){
		connlatch = new CountDownLatch(1);
		this.resourceName = resName;
		try {
			zk = new ZooKeeper(conStr, sessionTimeout, this);
			connlatch.await();
			Stat rootStat = zk.exists(root, false);
			if(rootStat == null){
				zk.create(root, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			
			parentPath = root + "/" + resourceName;
			Stat subStat = zk.exists( parentPath , false);
			if(subStat == null){
				zk.create(parentPath, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			
			myNode = zk.create(parentPath + "/" + nodePrefix, new byte[0],
					ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			
		} catch (IOException | InterruptedException | KeeperException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isLeader(){
		boolean isLeader = false;
		List<String> list;
		try {
			list = zk.getChildren(parentPath, true);
			if(list != null ){
				logger.info(list.toString());
				Collections.sort(list);
				String minNode = list.get(0);
				String myLeafNode = myNode.substring((parentPath+"/").length());
				if (minNode.equals(myLeafNode)) {
					logger.info("isLead now! minNode : {},myNode: {}",minNode,myNode);
					return true;
				}
			}
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return isLeader;
	}
	
	public void checkLeader(){
		if(isLeader()){
			doLeader();
		}else{
			doFollowing();
		}
	}
	
	
	public void findLeader(){
		checkLeader();
	}
	
	public void doFollowing(){
		logger.info("{} do following~",myNode);
	}
	
	public void doLeader(){
		try {
			isNeedCheck = false;
			Thread.sleep(1000);
			logger.info("{} do leader~",myNode);
			Thread.sleep((long) (Math.random() *4000));
			shutdown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void shutdown(){
		if(zk != null){
			try {
				isRunning = false;
				zk.close();
				logger.info("{} shutdown now",Thread.currentThread().getName());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void process(WatchedEvent event) {
		if(isRunning){
			logger.info(event.toString());
			KeeperState stat = event.getState();
			if( Event.KeeperState.SyncConnected.equals(stat)){
				if( event.getType().equals(Event.EventType.None) ){
					connlatch.countDown();
					logger.info("zk connected!");
				}else if( event.getType().equals(Event.EventType.NodeChildrenChanged)) {
					if(isNeedCheck){
						checkLeader();
					}
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		final CountDownLatch beginLatch = new CountDownLatch(1);
		int threadNum = 20;
		
		for(int i=0; i<threadNum; i++){
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					String conStr = "192.168.88.8:2180";
					GroupMember gm  = new GroupMember(conStr,10000,"etl");
					try {
						beginLatch.await();
						gm.findLeader();
						Logger logger = LoggerFactory.getLogger(getClass());
						Thread.sleep(60000);
					//	gm.shutdown();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
			t.start();
		}
		
		beginLatch.countDown();
	}

}
