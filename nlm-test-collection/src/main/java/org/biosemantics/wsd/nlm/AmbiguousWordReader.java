package org.biosemantics.wsd.nlm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class AmbiguousWordReader {

	private static final String SEPERATOR = "\\|";
	private String filePath;

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Collection<AmbiguousWord> getAmbiguousWords() throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(new File(filePath)));
		List<AmbiguousWord> ambiguousWords = new ArrayList<AmbiguousWord>();
		for (Entry<Object, Object> entry : props.entrySet()) {
			String text = ((String) entry.getKey()).trim();
			String[] cuis = ((String) entry.getValue()).split(SEPERATOR);
			ambiguousWords.add(new AmbiguousWord(text, cuis));
		}
		return ambiguousWords;
	}
}
