package com.colin.java.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleFileReader {
	public static void main(String[] args) throws IOException {
		InputStreamReader in = new InputStreamReader(
				SimpleFileReader.class.getResourceAsStream("MessID.txt"));
		BufferedReader bin = new BufferedReader(in);

		StringBuffer sb = new StringBuffer();

		String line = bin.readLine();

		while (line != null) {
			if (line.indexOf("class=MsoNormal") != -1) {
				
				System.out.println(line.replaceAll("<P class=MsoNormal>", "").replaceAll("<SPAN", "").trim());
			}

			line = bin.readLine();
		}

		System.out.println(sb.toString());
	}

}
