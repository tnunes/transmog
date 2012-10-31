package org.biosemantics.disambiguation.umls.wsd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class AmbiguousWordReader {

	private static final String PATH = "../../../../../ambiguous.properties";
	private static final String SEPERATOR = "\\|";

	public Collection<AmbiguousWord> getAmbiguousWords() throws IOException {
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream(PATH));
		List<AmbiguousWord> ambiguousWords = new ArrayList<AmbiguousWord>();
		for (Entry<Object, Object> entry : props.entrySet()) {
			String text = ((String) entry.getKey()).trim();
			String[] cuis = ((String) entry.getValue()).split(SEPERATOR);
			ambiguousWords.add(new AmbiguousWord(text, cuis));
		}
		return ambiguousWords;
	}
	
	
	public static void main(String[] args) throws IOException {
		AmbiguousWordReader ambiguousWordReader = new AmbiguousWordReader();
		Collection<AmbiguousWord> ambiguousWords = ambiguousWordReader.getAmbiguousWords();
		for (AmbiguousWord ambiguousWord : ambiguousWords) {
			System.err.println(ambiguousWord.toString());
		}
	}
}
