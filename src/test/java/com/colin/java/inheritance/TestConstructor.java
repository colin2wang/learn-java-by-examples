package com.colin.java.inheritance;

public class TestConstructor {
	static class A {
		static String className = "a";

		A() {
			System.out.println(this.getClass());
		}

		public static void foo() {
			System.out.println("foo-" + className + "()");
		}
	}

	static class B extends A {
		static String className = "b";

		B(String name) {

		}

		public static void foo() {
			System.out.println("foo-" + className + "()");
		}
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		B b = new B("");
		B.foo();

	}
}
