package com.colin.java.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlwaysRun implements Runnable {
	private static Integer count = 0;
	
	@Override
	public void run() {
		synchronized (count) {
			++count;
		}
	}

	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		for (int i = 0; i < 100000; i++) {
			service.execute(new AlwaysRun());
		}
		
		System.out.println(count);
	}
}
