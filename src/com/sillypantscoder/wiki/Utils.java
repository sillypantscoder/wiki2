package com.sillypantscoder.wiki;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
	public static String readFile(File f) {
		try {
			FileReader r = new FileReader(f);
			String s = "";
			int i;
			while ((i = r.read()) != -1) s += (char) i;
			r.close();
			return s;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
