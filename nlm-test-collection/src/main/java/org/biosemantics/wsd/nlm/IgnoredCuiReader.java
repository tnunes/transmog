package org.biosemantics.wsd.nlm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class IgnoredCuiReader {

	public void setIgnoredCuisFile(String ignoredCuisFile) {
		this.ignoredCuisFile = ignoredCuisFile;
	}

	public void init() throws FileNotFoundException, IOException {
		props.load(new FileInputStream(new File(ignoredCuisFile)));
	}

	public boolean isIgnoredCui(String cui) {
		return props.containsKey(cui);
	}

	private String ignoredCuisFile;
	private Properties props = new Properties();
}
