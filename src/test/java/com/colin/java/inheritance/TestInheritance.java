package com.colin.java.inheritance;

public class TestInheritance {
	public static void main(String[] args) {

	}
}

interface A {
	String name = "A";
}

interface B {
	String name = "B";
}

class AB implements A, B {
	void printName() {

	}
}