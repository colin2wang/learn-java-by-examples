package com.colin.java.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestLock {
	public static void main(String[] args) {
		ThreadA a = new ThreadA();
		ThreadA b = new ThreadA();
		a.start();
		b.start();
	}
}

class ThreadA extends Thread {
	Lock mLock;

	@Override
	public void run() {

		while (true) {

			mLock = new ReentrantLock();

			mLock.lock();
			try {
				System.out.println(this.getName());
			} finally {
//				mLock.unlock();
			}

		}
	}
}