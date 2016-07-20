package com.fly.learn.jdk7.featrues.forkjoin.demo1;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		ProductListGenerator generator = new ProductListGenerator();
		List<Product> products = generator.generate(200);

		Task task = new Task(products, 0, products.size(), 0.20);
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		forkJoinPool.execute(task);

		do {
			System.out.printf("Main: Thread Count: %d\n",
					forkJoinPool.getActiveThreadCount());
			System.out.printf("Main: Thread Steal: %d\n",
					forkJoinPool.getStealCount());
			System.out.printf("Main: Parallelism: %d\n",
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
		products.forEach((prod) -> System.out.println(prod));
	}
}
