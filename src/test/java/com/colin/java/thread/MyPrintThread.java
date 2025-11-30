package com.colin.java.thread;

public class MyPrintThread extends Thread {

	private static Object lock = new Object();

	private static boolean isFirstThread = true;


	public static void main(String[] args) {
		
		Thread a = new Thread() {
			int seed = 1;
			@Override
			public void run() {
				while(seed <= 100) {
					synchronized (lock) {
						
						System.out.println(seed);
						seed+=2;
						
						if (isFirstThread) {
							isFirstThread = false;
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
				
				System.out.println(Thread.currentThread().getName() + " Ending...");
			}
		};
		
		Thread b = new Thread() {
			int seed = 2;
			@Override
			public void run() {
				while(seed <= 100) {
					synchronized (lock) {
						
						System.out.println(seed);
						seed+=2;
						
						if (!isFirstThread) {
							isFirstThread = true;
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
				
				System.out.println(Thread.currentThread().getName() + " Ending...");
			}
		};
		
		a.start();
		b.start();
	}
}
