package com.colin.java.thread.pool;

import java.text.MessageFormat;

public class TestThreadPool {
	private ThreadPoolManager threadPoolManager = null;
	public Object testlock = new Object();
	public int count = 0;
	private final int jobs;
	private final int size;
	int debug = 2;

	public TestThreadPool(int threadPoolSize, int jobsNum) {
		this.size = threadPoolSize;
		this.jobs = jobsNum;
		System.out.println(MessageFormat.format("Thread pool size: {0}, pooled jobs: {1}", size, jobs));
		long timeNoPool = 0;
		long timePool = 0;
		count = 0;
		System.err.println("Begin of testing strategy -- no pool");
		long start = System.currentTimeMillis();
		try {
			for (int i = 0; i < jobs; i++) {
				CalculationTaskTest calculationTask = new CalculationTaskTest();
				new Thread(calculationTask).start();
			}
		} catch (OutOfMemoryError er) {
			System.out.println("No pool :OutOfMemoryError");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		sleepToWait(5);
		if (debug > 3)
			System.out.println("no pool time:"
					+ (System.currentTimeMillis() - start));
		timeNoPool = System.currentTimeMillis() - start;
		System.err.println("End of no pool test");
		count = 0;
		// start = System.currentTimeMillis();
		System.err.println("Begin of  creating pool");
		threadPoolManager = new ThreadPoolManager(size);
		System.err.println("End of  creating pool");
		System.err.println("Begin of testing the strategy  -- pool");
		// without the time for creating pool
		start = System.currentTimeMillis();
		try {
			for (int i = 0; i < jobs; i++) {
				CalculationTaskTest calculationTaskTest = new CalculationTaskTest();
				threadPoolManager.addTask(calculationTaskTest);
			}
		} catch (OutOfMemoryError er) {
			System.out.println("pool :OutOfMemoryError" + " "
					+ size + " " + jobs);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		sleepToWait(5);
		if (debug > 3)
			System.out.println("pool time:"
					+ (System.currentTimeMillis() - start));
		timePool = System.currentTimeMillis() - start;
		System.err.println("End of thread pool test");
		System.out.println("without pool: " + timeNoPool + "    with pool: "
				+ timePool);

		System.exit(0);

	}

	public void sleepToWait(long l) {
		while (true) {
			synchronized (testlock) {
				if (count == jobs)
					return;
			}
			try {
				// you can change it to wait end of all tasks
				Thread.sleep(l);
			} catch (Exception ex) {
			}
		}
	}

	public static void main(String[] args) {
		int poolSize = 20;
		int jobs = 200;

		if (args.length < 2) {
			System.err.println("Usage: java TestThreadPool "
					+ "<Size of ThreadPool> <jobs> ");
			System.exit(-1);
		}
		try {
			poolSize = Integer.parseInt(args[0]);
			jobs = Integer.parseInt(args[1]);
		} catch (Exception ex) {
			System.out.println("Please input integer.");
			System.exit(-1);
		}
		new TestThreadPool(poolSize, jobs);
	}

	private class CalculationTaskTest implements Task, Runnable {
		private boolean isEnd = true;

		public CalculationTaskTest() {
			isEnd = true;
		}

		public void setEnd(boolean flag) {
			isEnd = flag;
		}

		public void run() {
			int i = 1;
			int r = 1;

			try {
				for (int ii = 0; ii < 100; ii++)
					r = r + i * i;
			} catch (Exception ex) {
			}
			synchronized (testlock) {
				count++;
			}

		}

		public void startTask() throws Exception {
			run();
		}

		public void endTask() throws Exception {
			System.out.println(MessageFormat.format("{0} end task.", Thread.currentThread().getName()));
		}

		public boolean isEnd() {
			return isEnd;
		}
	}
}
