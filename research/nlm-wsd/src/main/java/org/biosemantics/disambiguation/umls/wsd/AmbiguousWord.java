package org.biosemantics.disambiguation.umls.wsd;

import java.io.Serializable;
import java.util.Arrays;

public class AmbiguousWord implements Serializable {
	private String text;
	private String[] cuis;

	public String getText() {
		return text;
	}

	public String[] getCuis() {
		return cuis;
	}

	public AmbiguousWord(String text, String[] cuis) {
		super();
		this.text = text;
		this.cuis = cuis;
	}

	@Override
	public String toString() {
		return "AmbiguousWord [text=" + text + ", cuis=" + Arrays.toString(cuis) + "]";
	}

}
