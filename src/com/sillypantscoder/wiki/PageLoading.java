package com.sillypantscoder.wiki;

import java.io.File;

public class PageLoading {
	public static void writeToFile(String name, Page p) {
		BitString bits = p.save();
		File targetFolder = new File("pages/" + name);
		if (!targetFolder.exists()) targetFolder.mkdirs();
		File targetFile = new File("pages/" + name + "/page.dat");
		bits.writeToFile(targetFile);
	}
	public static Page loadFromFile(String name) {
		File targetFile = new File("pages/" + name + "/page.dat");
		BitString s = BitString.readFromFile(targetFile);
		Page p = Page.load(s);
		return p;
	}
	public static void main(String[] args) {
		Page p = new Page.ContentPage(false, false, "Hi there");
		System.out.println(p.toString());
		writeToFile("Hi", p);
		p = loadFromFile("Hi");
		System.out.println(p.toString());
	}
}
