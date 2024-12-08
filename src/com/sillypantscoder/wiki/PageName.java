package com.sillypantscoder.wiki;

public class PageName {
	public String[] parts;
	public PageName(String[] parts) {
		this.parts = new String[parts.length];
		System.arraycopy(parts, 0, this.parts, 0, parts.length);
	}
	public String getTitle() {
		return parts[parts.length - 1];
	}
	public String getSubLinks() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < parts.length - 1; i++) {
			b.append("<div><small><a href=\"" + "../".repeat(i) + "..\">" + parts[i] + "</a> &gt;</small></div>");
		}
		return b.toString();
	}
	public String join() {
		return String.join("/", parts);
	}
	public PageName	then(String nextName) {
		String[] newParts = new String[parts.length + 1];
		System.arraycopy(parts, 0, newParts, 0, parts.length);
		newParts[parts.length] = nextName;
		return new PageName(newParts);
	}
	// Equivalence
	public boolean equals(Object other) {
		if (other instanceof PageName o) {
			if (parts.length != o.parts.length) return false;
			for (int i = 0; i < parts.length; i++) {
				if (! parts[i].equals(o.parts[i])) return false;
			}
			return true;
		} else return false;
	}
	public int hashCode() {
		int r = 0;
		for (int i = 0; i < parts.length; i++) {
			r ^= parts[i].hashCode();
			r += i;
		}
		return r;
	}
}
