package com.fly.learn.jdk7.featrues.forkjoin.demo1;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		ProductListGenerator generator = new ProductListGenerator();
		List<Product> products = generator.generate(500000);

		Task task = new Task(products, 0, products.size(), 0.20);
		
		//创建一个ForkJoinPool 默认为Runtime.availableProcessors 
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		ForkJoinPool forkJoinPool1 = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
		System.out.println(forkJoinPool.getPoolSize()==forkJoinPool.getPoolSize());
		//异步调用 主线程继续执行
		forkJoinPool.execute(task);
		//同步调用 主线程阻塞
		//forkJoinPool.invoke(task);
		;
		do {
			System.out.printf("%s: 线程数: %d\n",Thread.currentThread(),
					forkJoinPool.getActiveThreadCount());
			System.out.printf("%s: 窃取任务: %d\n",Thread.currentThread(),
					forkJoinPool.getStealCount());
			System.out.printf("%s: 并行线程: %d\n",Thread.currentThread(),
					forkJoinPool.getParallelism());
			try {
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!task.isDone());

		if (task.isCompletedNormally()) {
			System.out.printf("Main: The process has completed normally.\n");
		}
		forkJoinPool.shutdown();
	//	products.forEach((prod) -> System.out.println(prod));
	}
}
