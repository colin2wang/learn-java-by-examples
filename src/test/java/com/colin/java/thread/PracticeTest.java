package com.colin.java.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PracticeTest {

	public static void main(String[] args) {
		final DataPrint data = new DataPrint();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (data.upLetterFlag) {
					data.printLetter();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (data.numFlag) {
					data.printNun();
				}
			}
		}).start();

	}

	static class DataPrint {
		public boolean upLetterFlag = true;
		public boolean numFlag = true;
		int num = 1;
		int letter = 65;
		boolean flag = true;
		Lock lock = new ReentrantLock();
		Condition condLetter = lock.newCondition();
		Condition condNum = lock.newCondition();

		public void printLetter() {
			if (letter >= 90) {
				upLetterFlag = false;
				return;
			}
			lock.lock();
			try {
				if (flag) {
					condLetter.await();
				}
				System.out.println(Thread.currentThread().getName() + ":"
						+ (char) letter);
				letter++;
				Thread.sleep(100);
				flag = true;
				condNum.signal();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}

		}

		public void printNun() {
			if (num >= 52) {
				numFlag = false;
				return;
			}
			lock.lock();
			try {
				if (!flag) {
					condNum.await();
				}
				System.out
						.println(Thread.currentThread().getName() + ":" + num);
				num++;
				System.out
						.println(Thread.currentThread().getName() + ":" + num);
				num++;
				Thread.sleep(100);
				flag = false;
				condLetter.signal();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
	}
}