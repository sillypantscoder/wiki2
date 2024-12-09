package com.sillypantscoder.wiki;

import java.util.HashSet;
import java.util.Set;

public class PageName {
	public String[] parts;
	public PageName(String[] parts) {
		this.parts = new String[parts.length];
		System.arraycopy(parts, 0, this.parts, 0, parts.length);
	}
	public PageName(String[] parts, int offset) {
		this.parts = new String[parts.length - offset];
		System.arraycopy(parts, offset, this.parts, 0, parts.length - offset);
	}
	public String getTitle() {
		return parts[parts.length - 1].replace("_", " ");
	}
	public String getSubLinks() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < parts.length - 1; i++) {
			b.append("<div><a href=\"" + "../".repeat(i) + "..\">" + parts[i] + "</a> &gt;</div>");
		}
		return b.toString();
	}
	public String join() {
		return String.join("/", parts);
	}
	public String join(String delimiter) {
		return String.join(delimiter, parts);
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
	public PageName[] findSubpages(Set<PageName> keySet) {
		HashSet<PageName> names = new HashSet<PageName>(keySet);
		names.removeIf((n) -> {
			if (n.parts.length <= parts.length) return true;
			for (int i = 0; i < parts.length; i++) {
				if (! parts[i].equals(n.parts[i])) return true;
			}
			return false;
		});
		return names.toArray(new PageName[names.size()]);
	}
}
