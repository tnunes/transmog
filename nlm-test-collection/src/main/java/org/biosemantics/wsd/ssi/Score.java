package org.biosemantics.wsd.ssi;

import java.io.Serializable;

public class Score implements Serializable {
	private String unambiguousCui;
	private int minHierarchicalHops;
	private int minRelatedHops;

	public String getUnambiguousCui() {
		return unambiguousCui;
	}

	public int getMinHierarchicalHops() {
		return minHierarchicalHops;
	}

	public int getMinRelatedHops() {
		return minRelatedHops;
	}

	public Score(String unambiguousCui, int minHierarchicalHops, int minRelatedHops) {
		super();
		this.unambiguousCui = unambiguousCui;
		this.minHierarchicalHops = minHierarchicalHops;
		this.minRelatedHops = minRelatedHops;
	}

}
