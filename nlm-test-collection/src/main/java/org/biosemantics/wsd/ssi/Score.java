package org.biosemantics.wsd.ssi;

import java.io.Serializable;

public class Score implements Serializable {
	private String ambiguousCui;
	private int minHierarchicalHops;
	private int minRelatedHops;

	public int getMinHierarchicalHops() {
		return minHierarchicalHops;
	}

	public int getMinRelatedHops() {
		return minRelatedHops;
	}

	public String getAmbiguousCui() {
		return ambiguousCui;
	}

	public Score(String ambiguousCui, int minHierarchicalHops, int minRelatedHops) {
		super();
		this.ambiguousCui = ambiguousCui;
		this.minHierarchicalHops = minHierarchicalHops;
		this.minRelatedHops = minRelatedHops;
	}

}
