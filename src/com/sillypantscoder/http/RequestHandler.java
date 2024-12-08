package com.sillypantscoder.http;

public abstract class RequestHandler {
	public abstract HttpResponse get(HttpRequest req);
	public abstract HttpResponse post(HttpRequest req);
}
