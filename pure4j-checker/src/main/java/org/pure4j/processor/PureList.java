package org.pure4j.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class PureList {

	public static Set<String> loadPureLists() {
		try {
			Set<String> out = new HashSet<String>();
			load("/java-lang.pure", out);
			return out;
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load the pure lists: ",e);
		}
	}

	private static void load(String fileName, Set<String> out) throws IOException {
		InputStream is = PureList.class.getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			out.add(line.trim());
			line = br.readLine();
		}
	}
}
