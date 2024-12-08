package com.sillypantscoder.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpServer implements HttpHandler {
	public RequestHandler handler;
	public com.sun.net.httpserver.HttpServer server;
	public HttpServer(RequestHandler handler) {
		this.handler = handler;
		try {
			InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 9374);
			server = com.sun.net.httpserver.HttpServer.create(addr, 0);
			server.createContext("/", this);
			server.setExecutor(null);
			server.start();
			System.out.println("Server started at: https://" + addr.getHostName() + ":" + addr.getPort() + "/");
		} catch (IOException e) {
			System.out.println("Server failed to start:");
			e.printStackTrace();
		}
	}
	@Override
	public void handle(HttpExchange httpExchange) {
		try {
			if (httpExchange.getRequestMethod().equals("GET")) {
				handleGetRequest(httpExchange);
			}
			if (httpExchange.getRequestMethod().equals("POST")) {
				handlePostRequest(httpExchange);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	private void handleGetRequest(HttpExchange httpExchange) throws IOException {
		String path = httpExchange.getRequestURI().toString();
		HttpResponse response = handler.get(new HttpRequest(path, null));
		response.send(httpExchange);
	}
	private void handlePostRequest(HttpExchange httpExchange) throws IOException {
		String path = httpExchange.getRequestURI().toString();
		byte[] body = httpExchange.getRequestBody().readAllBytes();
		String bodys = new String(body, StandardCharsets.UTF_8);
		HttpResponse response = handler.post(new HttpRequest(path, bodys));
		response.send(httpExchange);
	}
}
