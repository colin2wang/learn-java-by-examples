package com.colin.java.test;

import java.util.Arrays;

public class MyArrayList<E> {

	private Object[] m_elements = new Object[0];
	private int m_length = 0;
	private static final int INCR_SIZE = 10;

	public int size() {
		return m_length;
	}

	public boolean isEmpty() {
		return m_length == 0;
	}

	public boolean contains(Object o) {
		return false;
	}

	private int[] indexsOf(Object o, boolean isOnlyFirst) {
		if (m_length == 0 || o == null) {
			return null;
		}

		int indexs[] = new int[m_length];
		int count = 0;

		for (int idx = 0; idx < m_length; idx++) {
			if (o.equals(m_elements[idx])) {
				if (isOnlyFirst) {
					return new int[] { idx };
				}
				indexs[count++] = idx;
			}
		}
		return indexs.length > 0 ? indexs : null;
	}

	public int indexOf(Object o) {
		int[] indexs = indexsOf(o, true);

		return indexs == null ? -1 : indexs[0];
	}

	public MyArrayList<E> add(E e) {
		if (e == null) {
			return this;
		}
		if (m_length + 1 > m_elements.length) {
			m_elements = Arrays.copyOf(m_elements, INCR_SIZE);
		}
		m_elements[m_length++] = e;

		return this;
	}

	public MyArrayList<E> add(E... arr) {
		if (arr == null) {
			return this;
		}
		if (m_length + arr.length > m_elements.length) {
			m_elements = Arrays.copyOf(m_elements, INCR_SIZE);
		}

		for (E e : arr) {
			m_elements[m_length++] = e;
		}
		return this;
	}

	public void remove(Object o) {
		if (o != null) {
			int idx = indexOf(o);
			if (idx != -1) {
				m_elements[idx] = null;
				reallocate();
			}
		}
	}

	public void removeAll(Object[] arr) {
		boolean isNeedReallocate = false;
		for (Object o : arr) {
			int[] indexs = indexsOf(o, false);
			if (indexs != null) {
				isNeedReallocate = true;
				for (int idx : indexs) {
					m_elements[idx] = null;
				}
			}
		}

		if (isNeedReallocate) {
			reallocate();
		}

	}

	private void reallocate() {
		int newSize = 0;
		for (int idx = 0; idx < m_length; idx++) {
			if (m_elements[idx] != null) {
				newSize++;
			}
		}

		newSize = (newSize / INCR_SIZE + newSize % INCR_SIZE == 0 ? 0 : 1)
				* INCR_SIZE;

		Object[] oldElements = m_elements;
		m_elements = new Object[newSize];
		m_length = 0;

		for (Object o : oldElements) {
			if (o != null) {
				m_elements[m_length++] = o;
			}
		}
	}

	public void clear() {
		m_elements = new Object[0];
		m_length = 0;
	}

	@Override
	public String toString() {
		return Arrays.toString(Arrays.copyOfRange(m_elements, 0, m_length));
	}

	public static void main(String[] args) {
		MyArrayList list = new MyArrayList();

		list.add('c').add('b').add('e').add('c').add('a', 'b', 'c', 'a');

		System.out.println(list);
		
		list.remove('b');
		
		System.out.println(list);
		
		list.removeAll(new Object[] { 'a', 'a', 'c' });

		System.out.println(list);
	}
}
