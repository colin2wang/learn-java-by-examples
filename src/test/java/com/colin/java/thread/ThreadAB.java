package com.colin.java.thread;

public class ThreadAB extends Thread {
	private int seed = 0;
	private static Object lock = new Object();

	public ThreadAB(int seed) {
		this.seed = seed;
	}

	public void run() {
		synchronized (lock) {
			while (true) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				if (seed % 2 == 0) {
					lock.notify();
					System.out.println("Thread--" + currentThread().getName()
							+ "...." + seed);
					seed+=2;
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (seed % 2 == 1) {
					lock.notify();
					System.out.println("Thread--" + currentThread().getName()
							+ "...." + seed);
					seed+=2;
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		ThreadAB a = new ThreadAB(1);
		ThreadAB b = new ThreadAB(2);
		a.start();
		b.start();
	}
}