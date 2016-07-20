package com.fly.learn.jdk7.featrues;

import java.util.List;
import java.util.Map;


public class TestCollection {
	public static void main(String[] args) {
		int num1 = 0B1011_1111;
		int num2 = 1212_2344;
		System.out.println(num1);
		System.out.println(num2);
		
		String key = "aa";
		switch (key ) {
		case "aa":
			System.out.println(key);
			break;
		default:
			break;
		}
		
		boolean xor = Boolean.logicalXor(false, true);
		System.out.println(xor);
	}
}
