package com.sillypantscoder.wiki;

import java.io.File;

public class Page {
	public String name;
	public boolean isProtected;
	public Page(String name, boolean isProtected) {
		this.name = name;
		this.isProtected = isProtected;
	}
	public BitString getPageType() { return new BitString(false, false); }
	public String getContent() { return "This page does not exist"; }
	public BitString save() { return getPageType().append(this.isProtected); }
	public String toString() { return "Empty Page { name: " + name + "; protected: " + isProtected + " }"; }
	public static class RedirectPage extends Page {
		public String target;
		public RedirectPage(String name, boolean isProtected, String target) {
			super(name, isProtected);
			this.target = target;
		}
		public BitString getPageType() { return new BitString(false, true); }
		public String getContent() { return "Redirect: [[" + target + "]]"; }
		public BitString save() { return super.save().append(target); }
		public String toString() { return "Redirect Page { name: " + name + "; protected: " + isProtected + "; target: " + target + " }"; }
	}
	public static class ContentPage extends Page {
		public boolean isDraft;
		public String content;
		public ContentPage(String name, boolean isProtected, boolean isDraft, String content) {
			super(name, isProtected);
			this.isDraft = isDraft;
			this.content = content;
		}
		public BitString getPageType() { return new BitString(true, !this.isDraft); }
		public String getContent() { return this.content; }
		public BitString save() { return super.save().append(content); }
		public String toString() { return "Content Page { name: " + name + "; protected: " + isProtected + "; is draft: " + isDraft + "; content: " + content + " }"; }
	}
	public static Page load(String name, BitString bits) {
		if (bits.read()) {
			// Content Page
			boolean isDraft = !bits.read();
			boolean isProtected = bits.read();
			String contents = bits.readString();
			return new ContentPage(name, isProtected, isDraft, contents);
		} else {
			// Simple Page
			if (bits.read()) {
				// Redirect
				boolean isProtected = bits.read();
				String target = bits.readString();
				return new RedirectPage(name, isProtected, target);
			} else {
				// None
				boolean isProtected = bits.read();
				return new Page(name, isProtected);
			}
		}
	}
	public static void main(String[] args) {
		Page p = new ContentPage("hi", false, true, "a");
		BitString s = p.save();
		System.out.println(s.write());
		Page q = Page.load("hi", s);
		BitString t = q.save();
		System.out.println(t.write());
		// save to file
		File saveFile = new File("test.dat");
		t.writeToFile(saveFile);
		BitString u = BitString.readFromFile(saveFile);
		System.out.println(u.write());
		Page r = Page.load("hi", u);
		BitString v = r.save();
		System.out.println(v.write());
	}
}
