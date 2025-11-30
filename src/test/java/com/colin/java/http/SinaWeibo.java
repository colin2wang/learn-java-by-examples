package com.colin.java.http;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class SinaWeibo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getBodyString("10960117"));
	}

	private static String getBodyString(String id) {
		String str = "";

		// TODO Auto-generated method stub
		HttpClient httpClient = new HttpClient();
		String url = "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack&ticket=ST-MTIzNzg5MDIwNw==-1310928209-ja-7203DF9E5F5592CFCA0AADFA6D57ABD6&retcode=0";
		PostMethod postMethod = new PostMethod(url);
		// ������������ֵ
		NameValuePair[] data = { new NameValuePair("service", "miniblog"),
				new NameValuePair("client", "ssologin.js(v1.3.9)"),
				new NameValuePair("entry", "miniblog"),
				new NameValuePair("encoding", "utf-8"),
				new NameValuePair("gateway", "1"),
				new NameValuePair("savestate", "7"),
				new NameValuePair("from", ""),
				new NameValuePair("useticket", "0"),
				new NameValuePair("username", "colin2wang@sina.com.cn"),
				new NameValuePair("password", "19840516") };
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