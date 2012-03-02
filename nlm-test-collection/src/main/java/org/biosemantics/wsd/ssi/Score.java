package org.biosemantics.wsd.ssi;

import java.io.Serializable;

public class Score implements Serializable {
	private String unambiguousCui;
	private float hierarchicalScore;
	private float relatedScore;

	public String getUnambiguousCui() {
		return unambiguousCui;
	}

	public float getHierarchicalScore() {
		return hierarchicalScore;
	}

	public float getRelatedScore() {
		return relatedScore;
	}

	public Score(String unambiguousCui, float hierarchicalScore, float relatedScore) {
		super();
		this.unambiguousCui = unambiguousCui;
		this.hierarchicalScore = hierarchicalScore;
		this.relatedScore = relatedScore;
	}

}
