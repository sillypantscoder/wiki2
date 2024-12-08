package com.sillypantscoder.wiki;

public class Page {
	public String name;
	public boolean isProtected;
	public Page(String name, boolean isProtected) {
		this.name = name;
		this.isProtected = isProtected;
	}
	public BitString getPageType() { return new BitString(false, false); }
	public BitString save() { return getPageType().append(this.isProtected); }
	public static class RedirectPage extends Page {
		public String target;
		public RedirectPage(String name, boolean isProtected, String target) {
			super(name, isProtected);
			this.target = target;
		}
		public BitString getPageType() { return new BitString(false, true); }
		public BitString save() { return super.save().append(target); }
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
		public BitString save() { return super.save().append(content); }
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
	}
}
