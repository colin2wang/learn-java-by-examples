package com.colin.java.thread;

public class MyMainClass {
	public static void main(String[] args) {
		Thread first = new TryThread("A ", "a ", 200L);
		Thread second = new TryThread("B ", "b ", 300L);
		Thread third = new TryThread("C ", "c ", 500L);
		first.start();
		second.start();
		third.start();
		try {
			// first.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class TryThread extends Thread {
	public TryThread(String firstName, String secondName, long delay) {
		this.firstName = firstName;
		this.secondName = secondName;
		aWhile = delay;
		setDaemon(true);
	}

	public void run() {
		try {
			while (total < 1000) {
				System.out.print(firstName);
				sleep(aWhile);
				total += aWhile;
				System.out.print(secondName + "\n");
			}
			System.out.print(secondName + " stoped.\n");
		} catch (InterruptedException e) {
			System.out.println(firstName + secondName + e);
		}
	}

	private String firstName;
	private String secondName;
	private long aWhile;
	private long total;
}

