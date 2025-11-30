package com.colin.java.throwable;

import java.util.ArrayList;
import java.util.List;

public class TestError {

	private long f(int n) {
		if (n == 1) {
			return 1;
		}
		return n + f(n - 1);
	}

	public static void main(String[] args) {
		List<Object> list = new ArrayList<Object>();
//		new TestError().f(6000);
		
		Double.valueOf("SSS");
		
		System.out.println("End");
	}
}
