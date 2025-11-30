package com.colin.java.inheritance;

public class TestStatic {

}

class SA {
	void foo() {
	}

	static void sFoo() {
	}
}

class SB extends SA {
	@Override
	void foo() {
	}

	static void sFoo() {
		
	}
}
