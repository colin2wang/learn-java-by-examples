package com.colin.java.thread;

import java.io.IOException;

public class ThreadTest {
	public static void main(String[] args) throws InterruptedException {
		System.out.println(Thread.currentThread().getName() + ": " + Thread.currentThread().getId());
		
		MyTestThread t1 = new MyTestThread();
		MyTestThread t2 = new MyTestThread();
//		WaitThread t3 = new WaitThread();
		t1.start();
		t2.start();
//		t3.start();
	}
}

class MyTestThread extends Thread {
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			byte buffer[] = new byte[512]; // ���뻺����
			try {
				int count = System.in.read(buffer);
				System.out.println(Thread.currentThread().getName() + ": " + Thread.currentThread().getId());
				System.out.print(getName() + " input: ");
				for (int i = 0; i < count-2; i++) {
					System.out.print(" " + buffer[i] + (char)buffer[i]);
				}
				System.out.println();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class WaitThread extends Thread {
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (true) {
			System.out.println("Please input...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}