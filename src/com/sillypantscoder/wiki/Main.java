package com.sillypantscoder.wiki;

import com.sillypantscoder.http.HttpServer;

public class Main {
	public static void main(String[] args) {
		new HttpServer(new MainServer());
	}
}
