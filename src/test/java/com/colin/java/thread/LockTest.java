package com.colin.java.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock test
 * 
 * @author eric
 * @date Aug 14, 2012 8:04:57 PM
 */
public class LockTest {
	private static int i;
	private static Lock lk = new ReentrantLock();

	public static void test() {
		List<Thread> list = new ArrayList<Thread>();
		int tcount = 3;
		// prepare threads
		for (int i = 0; i < tcount; i++) {
			list.add(new Thread(new TmpRunnable(), "t-" + i));
		}
		// start threads
		for (int i = 0; i < tcount; i++) {
			list.get(i).start();
		}
	}

	private static class TmpRunnable implements Runnable {
		@Override
		public void run() {
			lk.lock();
			try {
				printTime("begin");
				Thread.sleep(1000 * 1); // sleep a while, for test purpose
				printTime("end");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lk.unlock();
			}
		}
	}

	public static void printTime() {
		printTime("");
	}

	/**
	 * print thread name & time
	 * 
	 * @param info
	 *            additional info to print
	 */
	public synchronized static void printTime(String info) {
		System.out.printf("%s:\t%d,\t,%d,\t%s\n", Thread.currentThread()
				.getName(), ++i, System.currentTimeMillis() / 1000, info);
	}

	public static void main(String[] args) {
		test();
	}
}
