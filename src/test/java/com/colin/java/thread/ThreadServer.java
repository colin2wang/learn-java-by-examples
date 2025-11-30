package com.colin.java.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadServer {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(8080);
		final Socket connection = socket.accept();
		socket.close();
	}
}
