package com.colin.java.serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @use ʵ�ֶԶ�������л��ͷ����л�
 * @author Bird
 * 
 */
public class Data {
	public static void writeObject() throws Exception {
		Worm worm = new Worm("Bird");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				"d://worm.out"));
		out.writeObject(worm);
		out.close();// �رյ�ͬʱҲˢ������˻�����
	}

	public static void readObject() throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"d://worm.out"));
		Worm s2 = (Worm) in.readObject();
		System.out.println(s2);
		in.close();
	}

	public static void main(String[] args) throws Exception {
//		writeObject();
		readObject();
	}
}