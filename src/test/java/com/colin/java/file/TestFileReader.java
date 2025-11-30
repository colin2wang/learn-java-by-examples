package com.colin.java.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestFileReader {
	public static void main(String[] args) throws IOException {
		InputStreamReader in = new InputStreamReader(
				TestFileReader.class.getResourceAsStream("test.txt"));
		BufferedReader bin = new BufferedReader(in);

		StringBuffer sb = new StringBuffer();

		String temp = bin.readLine();

		List<Melody> melodyList = new ArrayList<Melody>();
		int index = 1;

		while (temp != null) {

			System.out.println("typeMap.put(" + temp.replace("\"=\"", "\", \"") + ");");

			temp = bin.readLine();
		}

		System.out.println(sb.toString());
	}

	static class Melody {
		private int pitch;
		private int dur;
		private float time;

		public Melody(int pitch, int dur, float time) {
			super();
			this.pitch = pitch;
			this.dur = dur;
			this.time = time;
		}

		public int getPitch() {
			return pitch;
		}

		public void setPitch(int pitch) {
			this.pitch = pitch;
		}

		public int getDur() {
			return dur;
		}

		public void setDur(int dur) {
			this.dur = dur;
		}

		public float getTime() {
			return time;
		}

		public void setTime(float time) {
			this.time = time;
		}
	}
}
