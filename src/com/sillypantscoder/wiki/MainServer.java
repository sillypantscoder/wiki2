package com.sillypantscoder.wiki;

import java.io.File;
import java.util.HashMap;

import com.sillypantscoder.http.HttpRequest;
import com.sillypantscoder.http.HttpResponse;
import com.sillypantscoder.http.RequestHandler;

public class MainServer extends RequestHandler {
	public HashMap<PageName, Page> pages = PageLoading.getAllPages();
	public HttpResponse get(HttpRequest req) {
		if (req.next("wiki")) {
			if (req.next("main")) {
				PageName pageName = new PageName(req.path);
				if (! String.join("/", req.path).matches("^[a-zA-Z0-9_/]$")) new HttpResponse().setStatus(400).setBody("404 GET (The page name is invalid)");
				Page p;
				if (pages.containsKey(pageName)) {
					p = pages.get(pageName);
				} else {
					p = new Page(false);
					pages.put(pageName, p);
				}
				String html = loadPageMain(pageName, p);
				return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/html").setBody(html);
			}
		}
		return new HttpResponse().setStatus(404).setBody("404 GET");
	}
	public String loadPageMain(PageName name, Page p) {
		File template = new File("main.html");
		String templateHTML = Utils.readFile(template);
		String pageContentHTML = Wikitext.parse(p.getContent());
		String result = templateHTML
			.replace("{{SUBLINKS}}", name.getSubLinks())
			.replace("{{TITLE}}", name.getTitle())
			.replace("{{CONTENT}}", pageContentHTML);
		return result;
	}
	public HttpResponse post(HttpRequest req) {
		return new HttpResponse().setStatus(404).setBody("404 GET");
	}
}
