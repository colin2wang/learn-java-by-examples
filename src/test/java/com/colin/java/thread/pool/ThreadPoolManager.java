package com.colin.java.thread.pool;

import java.util.Vector;

public class ThreadPoolManager {

	/**
	 * the number of threads in pool
	 */
	private int threadNum;
	/**
	 * the vector of threads in pool
	 */
	private Vector<WorkThread> workThreadVector;
	/**
	 * the vector of tasks
	 */
	private Vector<Task> taskVector;

	/**
	 * @param i
	 */
	public ThreadPoolManager(int i) {
		taskVector = new Vector<Task>(10, 10);
		if (i > 0) {
		}
		// call thread
		CreateThreadPool(i);
	}

	public ThreadPoolManager() {
		this(10);
	}

	/**
	 *
	 * @return
	 */
	public boolean isAllTaskFinish() {
		return taskVector.isEmpty();
	}

	/**
	 * @return int
	 */
	public int getThreadNum() {
		return threadNum;
	}

	/**
	 * create thread pool
	 * 
	 * @param i
	 */
	private void CreateThreadPool(int i) {
		if (workThreadVector == null)
			workThreadVector = new Vector<WorkThread>(i);
		// create threads
		synchronized (workThreadVector) {
			for (int j = 0; j < i; j++) {
				threadNum++;
				WorkThread workThread = new WorkThread(taskVector, threadNum);
				workThreadVector.addElement(workThread);
			}

		}
	}

	/**
	 * add task to task vector and notify work Threads in pool to do it
	 * 
	 * @param taskObj
	 */
	public void addTask(Task taskObj) {
		if (taskObj == null)
			return;
		synchronized (taskVector) {
			taskVector.addElement(taskObj);
			taskVector.notifyAll();
		}
	}

	/**
	 * destroy threads in pool
	 */
	public void closeThread() {
		while (!workThreadVector.isEmpty()) {

			try {
				WorkThread workThread = (WorkThread) workThreadVector.remove(0);
				workThread.closeThread();
				continue;
			} catch (Exception exception) {

				exception.printStackTrace();
			}
			break;
		}
	}
}
