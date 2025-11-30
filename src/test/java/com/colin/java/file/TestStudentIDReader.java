package com.colin.java.file;

import com.colin.java.http.PostHttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestStudentIDReader {
	public static void main(String[] args) throws IOException {
		InputStreamReader in = new InputStreamReader(
				TestStudentIDReader.class.getResourceAsStream("ID.txt"));
		BufferedReader bin = new BufferedReader(in);

		StringBuffer sb = new StringBuffer();

		String line = bin.readLine();

		while (line != null) {
			sb.append(line);
			sb.append("\t");
			sb.append(PostHttp.getStudentName(line));
			sb.append("\n");
			
			line = bin.readLine();
		}
		
		System.out.println(sb.toString());
	}

}
