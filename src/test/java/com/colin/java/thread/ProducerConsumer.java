package com.colin.java.thread;

public class ProducerConsumer {
	public static void main(String[] args) {
		SyncStack ss = new SyncStack();
		Producer p = new Producer(ss);
		Consumer c = new Consumer(ss);
		new Thread(p).start();
		new Thread(c).start();
	}
}

class Wotou {
	int id;

	Wotou(int id) {
		this.id = id;
	}

	public String toString() {
		return "Wotou:" + id;
	}
}

class SyncStack {
	int index = 0;
	Wotou[] arrWT = new Wotou[6];

	public synchronized void push(Wotou wt) {
		while (index == arrWT.length) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				System.out.println("SyncStack push wait�����\n" + e.getMessage());
			}
		}

		this.notify(); // ���ж����������notifyAll()
		arrWT[index] = wt;
		index++;
	}

	public synchronized Wotou pop() {
		while (index == 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				System.out.println("SyncStack pop wait�����\n" + e.getMessage());
			}

		}
		this.notify();
		index--;
		return arrWT[index];
	}
}

class Producer implements Runnable {
	SyncStack ss = null;

	Producer(SyncStack ss) {
		this.ss = ss;
	}

	public void run() {
		for (int i = 0; i < 20; i++) {
			Wotou wt = new Wotou(i);
			ss.push(wt);
			System.out.println("�����ˣ�" + wt);
			try {
				Thread.sleep((int) (100 * Math.random()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Consumer implements Runnable {
	SyncStack ss = null;

	Consumer(SyncStack ss) {
		this.ss = ss;
	}

	public void run() {
		for (int i = 0; i < 20; i++) {
			Wotou wt = ss.pop();
			System.out.println("�����ˣ�" + wt);
			try {
				Thread.sleep((int) (100 * Math.random()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}