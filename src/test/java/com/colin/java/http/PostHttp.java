package com.colin.java.http;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
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
		
		// 创建OkHttpClient实例
		OkHttpClient httpClient = new OkHttpClient();
		
		// 构建URL
		String url = "https://home.cse.ust.hk/~csbb/Password_Only/csit561/grades.php?p=2";
		
		// 构建请求参数
		RequestBody requestBody = new FormBody.Builder()
				.add("stu_id", id)
				.add("section", "2")
				.add("B1", "Submit")
				.build();
		
		// 构建请求
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		
		// 执行请求
		try (Response response = httpClient.newCall(request).execute()) {
			int statusCode = response.code();

            log.info("statusCode: {}", statusCode);
			
			// 处理重定向
			if (statusCode == 301 || statusCode == 302) {
				// 从响应头获取重定向的地址
				String location = response.header("location");
				if (location != null) {
					log.info("The page was redirected to: {}", location);
				} else {
                    log.info("Location field value is null.");
				}
				return null;
			} else {
				// 获取响应体
				if (response.body() != null) {
					str = response.body().string();
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		return str;
	}

}