package com.fly.learn.jdk8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by FlyWeight on 2018/11/24.
 */
public class StreamTest {

    public static void testStream() {
        int sum = Stream.of("1", "2", "3")
                .filter((x) -> x.compareTo("2") >= 0)
                .mapToInt((x) -> Integer.valueOf(x)).sum();

        System.out.println(sum);
        System.out.println("============");
        Stream.of("a,f", "b,g", "c", "d").flatMap((s) -> Stream.of(s.split(","))).forEach(System.out::println);

        String[] arr = {"aa", "bb", "c", "cd"};
        List<String> arrList = Arrays.asList(arr);
        String result = arrList.stream().reduce("", (a, b) -> a + ",".concat(b));
        System.out.println(result);

    }


    public static void testSupplier() {
        Random rnd = new Random();
        Supplier<Integer> intSupplier = rnd::nextInt;
        Stream.generate(intSupplier).limit(10).sorted().forEach(System.out::println);
    }

    public static void main(String[] args) {

        testStream();
        testSupplier();
    }

}
