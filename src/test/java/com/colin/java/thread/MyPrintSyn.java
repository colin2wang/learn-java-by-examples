package com.colin.java.thread;

public class MyPrintSyn {

	private static Object lock = new Object();
	private static boolean flag = false;

	public static void main(String[] args) {

		Thread a = new Thread() {
			int seed = 1;

			public void run() {
				while (true) {
					synchronized (lock) {
						System.out.println(seed);
						seed += 2;

						if (flag) {
							flag = false;
							lock.notify();
							try {
								lock.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				}
			}
		};

		Thread b = new Thread() {
			int seed = 2;

			public void run() {
				while (true) {
					synchronized (lock) {
						System.out.println(seed);
						seed += 2;

						if (!flag) {
							flag = true;
							lock.notify();
							try {
								lock.wait();
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}

				}
			}
		};

		a.start();
		b.start();
	}
}