package com.sillypantscoder.wiki;

import java.io.File;

public class PageLoading {
	public static void writeToFile(Page p) {
		BitString bits = p.save();
		File targetFolder = new File("pages/" + p.name);
		if (!targetFolder.exists()) targetFolder.mkdirs();
		File targetFile = new File("pages/" + p.name + "/page.dat");
		bits.writeToFile(targetFile);
	}
	public static Page loadFromFile(String name) {
		File targetFile = new File("pages/" + name + "/page.dat");
		BitString s = BitString.readFromFile(targetFile);
		Page p = Page.load(name, s);
		return p;
	}
	public static void main(String[] args) {
		Page p = new Page.ContentPage("Hi", false, false, "Hi there");
		System.out.println(p.toString());
		writeToFile(p);
		p = loadFromFile("Hi");
		System.out.println(p.toString());
	}
}
