package com.sillypantscoder.wiki;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PageLoading {
	public static Page loadFromFile(PageName name) {
		File targetFile = new File("pages/" + name.join() + "/page.dat");
		BitString s = BitString.readFromFile(targetFile);
		Page p = Page.load(s);
		return p;
	}
	public static HashMap<PageName, Page> getAllPages() {
		HashMap<PageName, Page> pages = new HashMap<PageName, Page>();
		for (File f : new File("pages").listFiles()) {
			PageName pageName = new PageName(new String[] { f.getName() });
			loadPageAndChildren(pageName, pages);
		}
		return pages;
	}
	public static void loadPageAndChildren(PageName pageName, HashMap<PageName, Page> pages) {
		Page p = loadFromFile(pageName);
		pages.put(pageName, p);
		for (File f : new File("pages/" + pageName.join()).listFiles()) {
			if (! f.isDirectory()) continue;
			PageName childName = pageName.then(f.getName());
			loadPageAndChildren(childName, pages);
		}
	}
	// Saving
	public static void saveAllPages(HashMap<PageName, Page> pages) {
		ArrayList<PageName> names = new ArrayList<PageName>(pages.keySet());
		for (int i = 0; i < names.size(); i++) {
			PageName name = names.get(i);
			writeToFile(name, pages.get(name));
		}
	}
	public static void writeToFile(PageName name, Page p) {
		BitString bits = p.save();
		File targetFolder = new File("pages/" + name.join());
		if (!targetFolder.exists()) targetFolder.mkdirs();
		File targetFile = new File("pages/" + name.join() + "/page.dat");
		bits.writeToFile(targetFile);
	}
	public static void main(String[] args) {
		Page p = new Page.ContentPage(false, false, "Hi there");
		System.out.println(p.toString());
		writeToFile(new PageName(new String[] { "Hi", "asdf" }), p);
		p = loadFromFile(new PageName(new String[] { "Hi", "asdf" }));
		System.out.println(p.toString());
	}
}
