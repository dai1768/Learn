package com.fly.learn.jdk7.featrues;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author piaohailin
 * @date   2014-3-26
*/
public class Fibonacci extends RecursiveTask<Long> {
    private static final long serialVersionUID = 7875142223684511653L;
    private final long        n;

    Fibonacci(long n) {
        this.n = n;
    }

    protected Long compute() {
        if (n <= 1) {
            return n;
        }
        Fibonacci f1 = new Fibonacci(n - 1);
        f1.fork();
        Fibonacci f2 = new Fibonacci(n - 2);
        return f2.compute() + f1.join(); 
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Fibonacci task = new Fibonacci(10);
        ForkJoinPool pool = new ForkJoinPool(4);

        pool.invoke(task);
        System.out.println(task.get());
        System.out.println(task.getRawResult());
    }

}
