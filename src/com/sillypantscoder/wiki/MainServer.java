package com.sillypantscoder.wiki;

import com.sillypantscoder.http.HttpRequest;
import com.sillypantscoder.http.HttpResponse;
import com.sillypantscoder.http.RequestHandler;

public class MainServer extends RequestHandler {
	public HttpResponse get(HttpRequest req) {
		if (req.next("wiki")) {
			if (req.next("main")) {
				String pageName = req.next();
				if (pageName.equals("")) {}
			}
		}
		return new HttpResponse().setStatus(404).setBody("404 GET");
	}
	public HttpResponse post(HttpRequest req) {
		return new HttpResponse().setStatus(404).setBody("404 GET");
	}
}