package com.colin.java.http;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SinaWeibo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getBodyString("10960117"));
	}

	private static String getBodyString(String id) {
		String str = "";

		// 创建OkHttpClient实例
		OkHttpClient httpClient = new OkHttpClient();
		
		// 构建URL
		String url = "http://weibo.com/ajaxlogin.php";
		
		// 构建请求参数
		RequestBody requestBody = new FormBody.Builder()
				.add("service", "miniblog")
				.add("client", "ssologin.js(v1.3.9)")
				.add("entry", "miniblog")
				.add("encoding", "utf-8")
				.add("gateway", "1")
				.add("savestate", "7")
				.add("from", "")
				.add("useticket", "0")
				.add("username", "colin2wang@sina.com.cn")
				.add("password", "19840516")
				.build();
		
		// 构建请求
		HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
		httpUrlBuilder.addQueryParameter("framelogin", "1");
		httpUrlBuilder.addQueryParameter("callback", "parent.sinaSSOController.feedBackUrlCallBack");
		httpUrlBuilder.addQueryParameter("ticket", "ST-MTIzNzg5MDIwNw==-1310928209-ja-7203DF9E5F5592CFCA0AADFA6D57ABD6");
		httpUrlBuilder.addQueryParameter("retcode", "0");
		
		Request request = new Request.Builder()
				.url(httpUrlBuilder.build())
				.post(requestBody)
				.build();
		
		// 执行请求
		try (Response response = httpClient.newCall(request).execute()) {
			int statusCode = response.code();
			
			// 处理重定向
			if (statusCode == 301 || statusCode == 302) {
				// 从响应头获取重定向的地址
				String location = response.header("location");
				if (location != null) {
					System.out.println("The page was redirected to:" + location);
				} else {
					System.err.println("Location field value is null.");
				}
				return null;
			} else {
				// 获取响应体
				if (response.body() != null) {
					str = response.body().string();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return str;
	}

}