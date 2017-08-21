package com.fly.learn.jdk7.featrues.forkjoin.demo1;

import java.util.List;
import java.util.concurrent.RecursiveAction;

public class Task extends RecursiveAction {

	private static final long serialVersionUID = 1L;

	private List<Product> products;
	private int first;
	private int last;
	private double increment;

	public Task(List<Product> products, int first, int last, double increment) {

		this.products = products;
		this.first = first;
		this.last = last;
		this.increment = increment;

	}

	@Override
	protected void compute() {
		if (last-first<100) {
			updatePrices();
		}else{
			int middle = (last + first ) /2;
			System.out.printf("%s,待处理的任务:	%s\n",Thread.currentThread(),getQueuedTaskCount());
			Task t1 = new Task(products, first,middle+1, increment);
			Task t2 = new Task(products, middle+1, last,increment);
			//同步接口，须等待子任务结束才返回
			super.invokeAll(t1,t2);
			
		}

	}

	private void updatePrices() {
		for (int i = first; i < last; i++) {
			Product product = products.get(i);
			product.setPrice(product.getPrice() * (1 + increment));
		}
	}
}
