package com.sillypantscoder.wiki;

import java.util.ArrayList;

public class BitString {
	public ArrayList<Boolean> bits;
	public BitString() {
		this.bits = new ArrayList<Boolean>();
	}
	public BitString(boolean... b) {
		this.bits = new ArrayList<Boolean>();
		this.append(b);
	}
	public BitString append(boolean b) {
		bits.add(b);
		return this;
	}
	public BitString append(boolean... b) {
		for (int i = 0; i < b.length; i++) bits.add(b[i]);
		return this;
	}
	public BitString append(String data) {
		for (int i = 0; i < data.length(); i++) {
			append(true);
			char d = data.charAt(i);
			for (int j = 0; j < 8; j++) {
				append((d & (1 << j)) != 0);
			}
		}
		append(false);
		return this;
	}
	public char getChar(int startLoc) {
		int b = 0;
		for (int j = 0; j < 8; j++) {
			if (startLoc + j >= bits.size()) break;
			if (bits.get(startLoc + j)) b |= 1 << j;
		}
		return (char)(b);
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bits.size(); i += 8) {
			sb.append(getChar(i));
		}
		return sb.toString();
	}
	public String write() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bits.size(); i++) {
			sb.append(bits.get(i) ? "1" : "0");
		}
		return sb.toString();
	}
	// Reading
	public boolean read() {
		if (bits.isEmpty()) return false;
		return bits.remove(0);
	}
	public String readString() {
		StringBuilder result = new StringBuilder();
		while (read()) {
			result.append(getChar(0));
			for (int i = 0; i < 8; i++) read();
		}
		return result.toString();
	}
	// Testing
	public static void main(String[] args) {
		// Test
		for (int i = 0; i < 100; i++) {
			BitString s = new BitString();
			s.append(true, false, true, false, true, true);
			System.out.print(s.bits.toString());
			System.out.println(" - " + s.toString());
		}
	}
}
