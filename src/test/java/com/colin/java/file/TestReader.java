package com.colin.java.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestReader {
	
	private static final Pattern messageParameterPattern = Pattern.compile(".+Var(\\d+).+");
	
	public static void main(String[] args) throws IOException {
		InputStreamReader in = new InputStreamReader(TestFileReader.class.getResourceAsStream("Reader.txt"));
		
		BufferedReader bin = new BufferedReader(in);

		String temp = bin.readLine();

		List<TestFileReader.Melody> melodyList = new ArrayList<TestFileReader.Melody>();
		int index = 1;

		while (temp != null) {
			
			Matcher matcher = messageParameterPattern.matcher(temp);
			
			while (matcher.find()) {
				String str1 = matcher.group(1);
				System.out.print(str1 + ", ");
			}
			
			temp = bin.readLine();
		}
		
	}
}
