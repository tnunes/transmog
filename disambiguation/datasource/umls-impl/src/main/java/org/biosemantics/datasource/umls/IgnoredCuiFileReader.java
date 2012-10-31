package org.biosemantics.datasource.umls;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;

public class IgnoredCuiFileReader {
	
	
	
	private static final String IGNORED_CUI_FILE = "/ignored-cuis.txt";
	private Collection<String> ignoredCuis = new ArrayList<String>();
	
	
	public IgnoredCuiFileReader() throws IOException {
		InputStream inputStream = this.getClass().getResourceAsStream(IGNORED_CUI_FILE);
		if (inputStream == null) {
			System.err.println("ignored-cuis.txt file not found!");
		} else {
			ignoredCuis = IOUtils.readLines(inputStream);
		}
	}


	public Collection<String> getIgnoredCuis() {
		return ignoredCuis;
	}
	

}
