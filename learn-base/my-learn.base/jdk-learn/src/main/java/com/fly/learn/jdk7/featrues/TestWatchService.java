package com.fly.learn.jdk7.featrues;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * 文件系统监视
 * @author FlyWeight
 *
 */
public class TestWatchService {

	private WatchService watchService;

	public TestWatchService(Path path) {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			path.register(watchService, ENTRY_CREATE, ENTRY_DELETE,
					ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doMonitor() {
		WatchKey key = null;
		boolean isVaid = false;
		while (true) {
			try {
				key = watchService.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					System.out.println("Event kind " + event.kind() + " count : " + event.count() + " event context : ");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isVaid = key.reset();
			if (!isVaid) {
				break;
			}
		}
	}

	public static void main(String[] args) {
		Path path = Paths.get("F:/test");
		System.out.println(path.getRoot());
		System.out.println(path.getFileSystem().getSeparator());
		TestWatchService service = new TestWatchService(path);
		service.doMonitor();
	}

}
