package org.biosemantics.wsd.ssi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SsiScore implements Serializable {

	private static final long serialVersionUID = 6800059274242873839L;
	private static final double CONSTANT = .9F;
	private String unambiguousCui;
	private List<Score> scores;

	public List<Score> getScores() {
		return scores;
	}

	public String getUnambiguousCui() {
		return unambiguousCui;
	}

	public SsiScore(String unambiguousCui, List<Score> scores) {
		super();
		this.unambiguousCui = unambiguousCui;
		this.scores = scores;
	}
}
