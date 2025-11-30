package com.colin.java.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyPrintLock implements Runnable {

	private int tnum = 1;

	private ReentrantLock lock = new ReentrantLock();

	private Condition redCon = lock.newCondition();
	private Condition greenCon = lock.newCondition();

	public static void main(String[] args) {
		new MyPrintLock().run();
	}

	@Override
	public void run() {
		new Thread(new RedThread(), "red light").start();
		new Thread(new GreenThread(), "green light").start();
	}

	class RedThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					lock.lock();
					while (tnum != 1) {
						redCon.await();
					}
					System.out.println(Thread.currentThread().getName()
							+ " is flashing...");

					TimeUnit.SECONDS.sleep(1);

					tnum = 2;
					greenCon.signal();

				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		}
	}

	class GreenThread implements Runnable {

		@Override
		public void run() {

			while (true) {
				try {
					lock.lock();
					while (tnum != 2) {
						greenCon.await();
					}
					System.out.println(Thread.currentThread().getName()
							+ " is flashing...");

					TimeUnit.SECONDS.sleep(1);

					tnum = 1;
					redCon.signal();

				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		}

	}

}