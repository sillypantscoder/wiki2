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
			if (! String.join("/", req.path).matches("^[a-zA-Z0-9_/]+$")) return new HttpResponse().setStatus(400).setBody("400 GET (invalid page name)");
			PageName pageName = new PageName(req.path, 1);
			Page p = getPage(pageName, false);
			// find what html to use
			// based on modifier
			String html;
			if (req.next("main")) {
				html = loadPageMain(pageName, p);
			} else if (req.next("info")) {
				html = loadPageInfo(pageName, p);
			} else if (req.next("edit")) {
				html = loadPageEdit(pageName, p);
			} else {
				// invalid modifier
				return new HttpResponse().setStatus(400).setBody("400 GET (invalid modifier)");
			}
			// return the html
			return new HttpResponse().setStatus(200).addHeader("Content-Type", "text/html").setBody(html);
		}
		// 404
		return new HttpResponse().setStatus(404).setBody("404 GET");
	}
	public Page getPage(PageName pageName, boolean createIfAbsent) {
		if (pages.containsKey(pageName)) {
			return pages.get(pageName);
		} else {
			Page p = new Page(false);
			if (createIfAbsent) pages.put(pageName, p);
			return p;
		}
	}
	public String loadPageMain(PageName name, Page p) {
		// Load template html
		File template = new File("main.html");
		String templateHTML = Utils.readFile(template);
		// Find page content
		String pageContentWT = p.getContent();
		if (p instanceof Page.ContentPage con) {
			if (con.isDraft) {
				// TODO: settings menu (add this)
				String templateContents = getPage(new PageName(new String[] { "Templates", "Draft" }), true).getContent();
				pageContentWT = templateContents + pageContentWT;
			}
		}
		String pageContentHTML = Wikitext.parse(pageContentWT);
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
		// Find subpages
		PageName[] subpages = name.findSubpages(pages.keySet());
		if (subpages.length == 0) {
			pageContentHTML += "<p>This page has no subpages</p>";
		} else {
			pageContentHTML += "<p>Subpages of this page:</p><ul>";
			for (PageName subpage : subpages) {
				pageContentHTML += "<li><a href=\"/wiki/main/" + subpage.join() + "\">" + subpage.join(" > ") + "</a></li>";
			}
			pageContentHTML += "</ul>";
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
	public String loadPageEdit(PageName name, Page p) {
		// Load template html
		File template = new File("main.html");
		String templateHTML = Utils.readFile(template);
		// Find page info data
		String pageContentHTML = "";
		if (p instanceof Page.RedirectPage red) {
			if (p.isProtected) pageContentHTML += "<p>This page is protected, you cannot edit it</p>";
			else pageContentHTML += "<p>Redirect target:<br><input type=text id=newValue value=\"" + red.target.replaceAll("[^a-zA-Z0-9_/]", "") +
				"><br><button onclick=\"submit(this.previousElementSibling.previousElementSibling.value)\">Submit</button></p>";
		} else if (p instanceof Page.ContentPage con) {
			if (p.isProtected) pageContentHTML += "<p>This page is protected, you cannot edit it</p>";
			else pageContentHTML += "<p>Content:<br><textarea>" + con.content.replace("&", "&amp;").replace("<", "&lt;") +
				"</textarea><br><button onclick=\"submit(this.previousElementSibling.previousElementSibling.value)\">Submit</button></p>";
		} else {
			if (p.isProtected) pageContentHTML += "<p>This page is protected, you cannot create it</p>";
			else pageContentHTML += "<p>Create page with:<br><textarea></textarea><br><button onclick=\"submit(this.previousElementSibling.previousElementSibling.value)\">Submit</button></p>";
		}
		pageContentHTML += "<script>function submit(data) { var x = new XMLHttpRequest(); x.open(\"POST\", \"/wiki/edit/" + name.join() +
			"\"); x.send(data); x.addEventListener(\"loadend\", () => location.replace(\"/wiki/main/" + name.join() + "\")); }</script>";
		// Find buttons
		StringBuilder buttons = new StringBuilder();
		buttons.append("<a href=\"/wiki/main/" + name.join() + "\">Cancel</a>");
		// Substitute content into template
		String result = templateHTML
			.replace("{{BUTTONS}}", buttons.toString())
			.replace("{{SUBLINKS}}", "")
			.replace("{{TITLE}}", "Edit \"" + name.join().replace("_", " ") + "\"")
			.replace("{{CONTENT}}", pageContentHTML);
		return result;
	}
	public HttpResponse post(HttpRequest req) {
		if (req.next("wiki")) {
			if (req.next("edit")) {
				if (! String.join("/", req.path).matches("^[a-zA-Z0-9_/]+$")) return new HttpResponse().setStatus(400).setBody("400 POST (invalid page name)");
				PageName pageName = new PageName(req.path);
				Page p = getPage(pageName, true);
				if (p.isProtected) return new HttpResponse().setStatus(403).setBody("Page is protected");
				if (p instanceof Page.RedirectPage red) {
					red.target = req.body;
				} else if (p instanceof Page.ContentPage con) {
					con.content = req.body;
				} else {
					// Create Draft
					Page.ContentPage newPage = new Page.ContentPage(p.isProtected, true, req.body);
					pages.put(pageName, newPage);
				}
				PageLoading.saveAllPages(pages);
				return new HttpResponse().setStatus(200).setBody("Successfully updated page content");
			}
		}
		return new HttpResponse().setStatus(404).setBody("404 POST");
	}
}
