package com.colin.java.thread.pool;

public interface Task {
	/**
	 * set status of task .
	 * 
	 * @param flag
	 */
	public void setEnd(boolean flag);

	/**
	 * start task
	 * 
	 * @throws java.lang.Exception
	 */
	public abstract void startTask() throws Exception;

	/**
	 * end tast
	 * 
	 * @throws java.lang.Exception
	 */
	public abstract void endTask() throws Exception;

	/**
	 * get status of task
	 * 
	 * @return boolean
	 */
	public boolean isEnd();

}
