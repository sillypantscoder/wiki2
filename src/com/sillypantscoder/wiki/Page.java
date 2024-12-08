package com.sillypantscoder.wiki;

public class Page {
	public boolean isProtected;
	public Page(boolean isProtected) {
		this.isProtected = isProtected;
	}
	public BitString getPageType() { return new BitString(false, false); }
	public String getContent() { return "This page does not exist"; }
	public BitString save() { return getPageType().append(this.isProtected); }
	public String toString() { return "Empty Page { protected: " + isProtected + " }"; }
	public static class RedirectPage extends Page {
		public String target;
		public RedirectPage(boolean isProtected, String target) {
			super(isProtected);
			this.target = target;
		}
		public BitString getPageType() { return new BitString(false, true); }
		public String getContent() { return "Redirect: [[" + target + "]]"; }
		public BitString save() { return super.save().append(target); }
		public String toString() { return "Redirect Page { protected: " + isProtected + "; target: " + target + " }"; }
	}
	public static class ContentPage extends Page {
		public boolean isDraft;
		public String content;
		public ContentPage(boolean isProtected, boolean isDraft, String content) {
			super(isProtected);
			this.isDraft = isDraft;
			this.content = content;
		}
		public BitString getPageType() { return new BitString(true, !this.isDraft); }
		public String getContent() { return this.content; }
		public BitString save() { return super.save().append(content); }
		public String toString() { return "Content Page { protected: " + isProtected + "; is draft: " + isDraft + "; content: " + content + " }"; }
	}
	public static Page load(BitString bits) {
		if (bits.read()) {
			// Content Page
			boolean isDraft = !bits.read();
			boolean isProtected = bits.read();
			String contents = bits.readString();
			return new ContentPage(isProtected, isDraft, contents);
		} else {
			// Simple Page
			if (bits.read()) {
				// Redirect
				boolean isProtected = bits.read();
				String target = bits.readString();
				return new RedirectPage(isProtected, target);
			} else {
				// None
				boolean isProtected = bits.read();
				return new Page(isProtected);
			}
		}
	}
}
