package org.zkdemo;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiTest implements Watcher {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	
	private void test(){
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper("192.168.88.8:2180",3000,this);
			
			String path = "/test1";
			String path1 = path + "/sub1";
			if(zk.exists(path, false) == null ){
				zk.create(path, "testdata".getBytes(),
						ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
			if(zk.exists(path1, false) == null){
				zk.create(path1, "subdata".getBytes(),
						ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
			
			
			zk.create("/test3", "tmp".getBytes(),
					ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
			
			List<String> children = zk.getChildren(path, this);
			logger.info("children : {} ",children.toString());
			States stat = zk.getState();
			logger.info("stat " + stat);
			
			byte[] data = zk.getData(path,this , null);
			logger .info("data : {}" ,new String(data));
			Thread.sleep(2000);
			zk.delete(path + "/sub1", -1);
			zk.delete(path, -1);
			zk.close();
		} catch (IOException | KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)  {
		ApiTest api = new ApiTest();
		api.test();
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("state:{} ,type:{} ,path :{} ",
				event.getState(),
				event.getType(),
				event.getPath()
				);
		System.out.println( );
	}
}
