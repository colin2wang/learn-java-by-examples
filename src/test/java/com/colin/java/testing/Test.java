package com.colin.java.testing;

public class Test {
	public static int lastZero(int[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.println("loop");
			if (x[i] == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int findLast(int[] x, int y) {
		for (int i = x.length - 1; i > 0; i--) {
			System.out.println("loop");
			if (x[i] == y) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		int a[] = {0};
		System.out.println(Test.findLast(a, 0));
	}

}
