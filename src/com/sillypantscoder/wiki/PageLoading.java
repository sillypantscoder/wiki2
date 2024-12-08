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
	public static Page loadFromFile(String name, String _previous) {
		File targetFile = new File("pages/" + name + "/page.dat");
		BitString s = BitString.readFromFile(targetFile);
		logBoth(_previous, s.write());
		Page p = Page.load(name, s);
		return p;
	}
	public static void logBoth(String previous, String next) {
		if (previous.indexOf(next) != -1) {
			int offset = previous.indexOf(next);
			System.out.println(previous);
			System.out.print(".".repeat(offset));
			System.out.println(next);
		} else if (next.indexOf(previous) != -1) {
			int offset = next.indexOf(previous);
			System.out.print(".".repeat(offset));
			System.out.println(previous);
			System.out.println(next);
		} else {
			System.out.print("................................");
			System.out.println(previous);
			System.out.println(next);
		}
	}
	public static void main(String[] args) {
		Page p = new Page.ContentPage("Hi", false, false, "Hi there");
		System.out.println(p.toString());
		String previous = p.save().write();
		writeToFile(p);
		p = loadFromFile("Hi", previous);
		System.out.println(p.toString());
	}
}
