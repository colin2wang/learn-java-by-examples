package com.colin.java.thread;

import java.text.DateFormat;
import java.util.Date;

public class YieldTest {
	public static void main(String[] args) throws InterruptedException {
		MyYieldThread r1 = new MyYieldThread();
		MyYieldThread r2 = new MyYieldThread();

		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);

		t1.start();
		t2.start();
		Thread.sleep(2000);
		System.exit(0);
	}

}

class MyYieldThread implements Runnable {
	final static boolean FLAG = true;
	volatile int i = 0;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!Thread.interrupted()) {
			Date date = new Date();
			DateFormat df = DateFormat.getTimeInstance();
			System.out.println(df.format(date));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
}
