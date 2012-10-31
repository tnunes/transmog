package org.biosemantics.wsd.nlm;

import java.io.Serializable;
import java.util.Arrays;

public class AmbiguousWord implements Serializable {

	private static final long serialVersionUID = 3200231233230091539L;
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
