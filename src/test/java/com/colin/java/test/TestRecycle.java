package com.colin.java.test;

public class TestRecycle {
	int index;
	static int count;

	TestRecycle() {
		count++;
		System.out.println("object " + count + " construct");
		setID(count);
	}

	void setID(int id) {
		index = id;
	}

	protected void finalize() {
		System.out.println("object " + index + " is reclaimed");
	}

	public static void main(String[] args) {
		new TestRecycle();
		new TestRecycle();
		new TestRecycle();
		new TestRecycle();
		System.gc();
	}
}