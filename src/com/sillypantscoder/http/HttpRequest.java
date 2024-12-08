package com.sillypantscoder.http;

public class HttpRequest {
	public String[] path;
	public String body;
	public HttpRequest(String path, String body) {
		String[] split = path.split("/", -1);
		this.path = new String[split.length - 1];
		System.arraycopy(split, 1, this.path, 0, this.path.length);
		this.body = body;
	}
	public boolean next(String part) {
		if (this.path[0].equals(part)) {
			String[] newPath = new String[path.length - 1];
			System.arraycopy(path, 1, newPath, 0, newPath.length);
			this.path = newPath;
			return true;
		}
		return false;
	}
	public String next() {
		String returnPart = this.path[0];
		this.next(returnPart);
		return returnPart;
	}
}
