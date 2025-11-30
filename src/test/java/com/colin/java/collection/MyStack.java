package com.colin.java.collection;

public class MyStack
{
	Object values[];
	int flag[];
	int index = 0;
	
	public MyStack()
	{
		flag = new int[300];
		values = new Object[300];
	}
	
	public int size(int num) // 0, 1, 2
	{
		int count = 0;
		for (int i=0; i<index; i++)
		{
			if (flag[i] == num)
			{
				count++;
			}
		}
		
		return count;
	}
	
	public boolean empty(int num) 
	{
		return size(num) == 0;
    }
	
	public void put(Object obj, int num)
	{
		flag[index] = num;
		values[index++] =  obj;
	}
	
	public Object pop(int num)
	{
		Object result = null;
		int mIndex = 0;
		for (int i=index-1; i>=0; i--)
		{
			if (flag[i] == num)
			{
				result = values[i];
				mIndex = i;
				index--;
				break;
			}
		}
		
		// Rearrange the flag and values index, since there is one element 
		// remove from the middle of the array.
		for (int i = mIndex; i<index; i++)
		{
			flag[i] = flag[i+1];
			values[i] = values[i+1];
		}
		
		return result;
	}
	
	public Object peek(int num)
	{
		for (int i=index-1; i>=0; i--)
		{
			if (flag[i] == num)
			{
				return values[i];
			}
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		MyStack m = new MyStack();
		
		m.pop(1);
		m.put("AAA", 1);
		m.put("BBB", 1);
		m.put("AAAA", 2);
		m.put("BBBB", 2);
		m.pop(2);
		m.put("CCCC", 2);
		m.put("CCC", 1);

		
		System.out.println(m.pop(1));
		System.out.println(m.pop(1));
		System.out.println(m.pop(1));
		
	}
}