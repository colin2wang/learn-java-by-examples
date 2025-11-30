package com.colin.java.serialize;

import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * @use �ڶ��ֿɿ������л����������ķ�ʽ
 * @author Bird
 * 
 */
public class Blip3 implements Externalizable {
	private int i;
	private String s;// û��ʵ����

	public Blip3() {
		System.out.println("Blip3 Constructor!!");
	}

	// ע�� �����sû��ʵ����

	public Blip3(String x, int a) {
		System.out.println("Blip3(String x, int a)");
		s = x;
		i = a;
		// s �� iʵ�����ڷ�Ĭ�Ϲ��캯����
	}

	public String toString() {
		return s + i;
	}

	public void writeExternal(ObjectOutput out) {// ��ѡ��д�����
		System.out.println("Blip3.writeExternal");
		try {
			out.writeObject(s);
			out.writeInt(i);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}// �����������������һ������Ҳ������ʼ��
	}

	public void readExternal(ObjectInput in) {// ��ѡ���������
		System.out.println("Blip3.readExternal");
		try {
			s = (String) in.readObject();
			i = in.readInt();
		} catch (ClassNotFoundException e) {

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void read() throws FileNotFoundException, IOException,
			ClassNotFoundException {// ��ȡ���л�����
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"d://Blip3.out"));
		System.out.println("Revovering  b3");
		Blip3 b3 = (Blip3) in.readObject();
		System.out.println(b3);
		in.close();
	}

	public void write() throws Exception {// д�����
		Blip3 b3 = new Blip3("A String", 47);
		System.out.println(b3);
		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(
				"d://Blip3.out"));
		System.out.println("Saving Object");
		o.writeObject(b3);
		o.close();
	}

	public static void main(String[] args) throws Exception {
		Blip3 b = new Blip3();
		// b.write();
		b.read();
	}
}
