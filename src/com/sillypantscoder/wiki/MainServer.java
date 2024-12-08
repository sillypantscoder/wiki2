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
				Page p = getPage(pageName);
				String html = loadPageMain(pageName, p);
				return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/html").setBody(html);
			} else if (req.next("info")) {
				PageName pageName = new PageName(req.path);
				Page p = getPage(pageName);
				String html = loadPageInfo(pageName, p);
				return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/html").setBody(html);
			}
		}
		return new HttpResponse().setStatus(404).setBody("404 GET");
	}
	public Page getPage(PageName pageName) {
		if (! String.join("/", pageName.parts).matches("^[a-zA-Z0-9_/]$")) new HttpResponse().setStatus(400).setBody("404 GET (The page name is invalid)");
		if (pages.containsKey(pageName)) {
			return pages.get(pageName);
		} else {
			Page p = new Page(false);
			pages.put(pageName, p);
			return p;
		}
	}
	public String loadPageMain(PageName name, Page p) {
		// Load template html
		File template = new File("main.html");
		String templateHTML = Utils.readFile(template);
		// Find page content
		String pageContentHTML = Wikitext.parse(p.getContent());
		// Find buttons
		StringBuilder buttons = new StringBuilder();
		if (!p.isProtected) {
			if (p instanceof Page.RedirectPage) {
				buttons.append("<a href=\"/wiki/edit/" + name.join() + "\">Edit redirect</a>");
			} else if (p instanceof Page.ContentPage) {
				buttons.append("<a href=\"/wiki/edit/" + name.join() + "\">Edit</a>");
			} else {
				buttons.append("<a href=\"/wiki/edit/" + name.join() + "\">Create page</a>");
			}
		}
		buttons.append("<a href=\"/wiki/info/" + name.join() + "\">Page info</a>");
		// Substitute content into template
		String result = templateHTML
			.replace("{{BUTTONS}}", buttons.toString())
			.replace("{{SUBLINKS}}", name.getSubLinks())
			.replace("{{TITLE}}", name.getTitle())
			.replace("{{CONTENT}}", pageContentHTML);
		return result;
	}
	public String loadPageInfo(PageName name, Page p) {
		// Load template html
		File template = new File("main.html");
		String templateHTML = Utils.readFile(template);
		// Find page info data
		String pageContentHTML = "";
		if (p instanceof Page.RedirectPage red) {
			pageContentHTML += "<p>This page is a redirect</p>";
			if (p.isProtected) pageContentHTML += "<p>This page is protected</p>";
			pageContentHTML += "<p>Redirect target: " + red.target + "</p>";
		} else if (p instanceof Page.ContentPage con) {
			pageContentHTML += "<p>This page is a " + (con.isDraft ? "draft" : "regular page") + "</p>";
			if (p.isProtected) pageContentHTML += "<p>This page is protected</p>";
			pageContentHTML += "<p>Content:<br><textarea readonly>" + con.content.replace("&", "&amp;").replace("<", "&lt;") + "</textarea></p>";
		} else {
			pageContentHTML += "<p>This page does not exist</p>";
			if (p.isProtected) pageContentHTML += "<p>This page is protected</p>";
		}
		// Find buttons
		StringBuilder buttons = new StringBuilder();
		buttons.append("<a href=\"/wiki/main/" + name.join() + "\">Back to page</a>");
		// Substitute content into template
		String result = templateHTML
			.replace("{{BUTTONS}}", buttons.toString())
			.replace("{{SUBLINKS}}", "")
			.replace("{{TITLE}}", "Info about \"" + name.join() + "\"")
			.replace("{{CONTENT}}", pageContentHTML);
		return result;
	}
	public HttpResponse post(HttpRequest req) {
		return new HttpResponse().setStatus(404).setBody("404 GET");
	}
}
