package com.colin.java.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadPool {
	
	public static int count = 0;

	public static void main(String[] args) {

		ExecutorService threadPool = Executors.newFixedThreadPool(5);

		for (int i = 0; i < 50; i++) {
			threadPool.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					System.out.println(count++);
				}
			});
		}
		
		threadPool.shutdown();
	}
}
