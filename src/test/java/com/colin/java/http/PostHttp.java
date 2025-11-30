package com.colin.java.http;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class PostHttp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getStudentName("10960117"));
	}

	public static String getStudentName(String id) {
		String bodyTextSpilt[] = getBodyString(id).split("\n");
		
		for (String line : bodyTextSpilt) {
			if (line.indexOf(id) != -1 && line.indexOf("Grades for Student") != -1) {
				return line.replaceAll("</td>", "")
						   .replaceAll("<td>", "")
						   .replaceAll("Grades for Student", "")
						   .replaceAll(id, "").trim();
			}
		}
		
		return null;
	}

	private static String getBodyString(String id) {
		String str = "";
		
		// TODO Auto-generated method stub
		HttpClient httpClient = new HttpClient();
		String url = "https://home.cse.ust.hk/~csbb/Password_Only/csit561/grades.php?p=2";
		PostMethod postMethod = new PostMethod(url);
		// ������������ֵ
		NameValuePair[] data = { new NameValuePair("stu_id", id),
				new NameValuePair("section", "2"),
				new NameValuePair("B1", "Submit") };
		// ������ֵ����postMethod��
		postMethod.setRequestBody(data);
		// ִ��postMethod
		int statusCode = 0;
		try {
			statusCode = httpClient.executeMethod(postMethod);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// HttpClient����Ҫ����ܺ�̷����������POST��PUT�Ȳ����Զ�����ת��
		// 301����302
		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
				|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
			// ��ͷ��ȡ��ת��ĵ�ַ
			Header locationHeader = postMethod.getResponseHeader("location");
			String location = null;
			if (locationHeader != null) {
				location = locationHeader.getValue();
				System.out.println("The page was redirected to:" + location);
			} else {
				System.err.println("Location field value is null.");
			}
			return null;
		} else {
			try {
				str = postMethod.getResponseBodyAsString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		postMethod.releaseConnection();
		return str;
	}

}