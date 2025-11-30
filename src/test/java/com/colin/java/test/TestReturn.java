package com.colin.java.test;

public class TestReturn {
	public static void main(String[] args) {
		System.out.println(test());
	}
	
	static int returnIntValue() {
		System.out.println("returnIntValue");
		return 1;
	}
	
	private static int test() {
		try {
			return (returnIntValue());
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			System.out.println("finally");
		}
		System.out.println("after try");
		return 0;
	}
}
