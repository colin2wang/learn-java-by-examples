package com.colin.java.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
						
						log.info("{}", seed);
						seed+=2;
						
						if (isFirstThread) {
							isFirstThread = false;
							lock.notify();
							
							try {
								lock.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								log.error(e.getMessage(), e);
							}
						}
					}
				}
				
				log.info("{} Ending...", Thread.currentThread().getName());
			}
		};
		
		Thread b = new Thread() {
			int seed = 2;
			@Override
			public void run() {
				while(seed <= 100) {
					synchronized (lock) {
						
						log.info("{}", seed);
						seed+=2;
						
						if (!isFirstThread) {
							isFirstThread = true;
							lock.notify();
							
							try {
								lock.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
                                log.error(e.getMessage(), e);
							}
						}
					}
				}
				
				log.info("{} Ending...", Thread.currentThread().getName());
			}
		};
		
		a.start();
		b.start();
	}
}