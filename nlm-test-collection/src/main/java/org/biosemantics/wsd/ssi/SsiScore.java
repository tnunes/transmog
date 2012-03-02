package org.biosemantics.wsd.ssi;

import java.io.Serializable;
import java.util.List;

public class SsiScore implements Serializable {

	private static final long serialVersionUID = 6800059274242873839L;
	private String wordSense;
	private List<Score> scores;

	public String getWordSense() {
		return wordSense;
	}

	public List<Score> getScores() {
		return scores;
	}

	public SsiScore(String wordSense, List<Score> scores) {
		super();
		this.wordSense = wordSense;
		this.scores = scores;
	}

}
