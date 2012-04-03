package org.biosemantics.wsd.similarity;

import java.io.Serializable;

public class PathScore implements Serializable {

	private String nodeFrom;
	private String nodeTo;
	private int hierPaths;
	private int hierPathLength;
	private int relatedPaths;
	private int relatedPathLength;

	public String getNodeFrom() {
		return nodeFrom;
	}

	public PathScore(String nodeFrom, String nodeTo, int hierPaths, int hierPathLength, int relatedPaths,
			int relatedPathLength) {
		super();
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
		this.hierPaths = hierPaths;
		this.hierPathLength = hierPathLength;
		this.relatedPaths = relatedPaths;
		this.relatedPathLength = relatedPathLength;
	}

	public String getNodeTo() {
		return nodeTo;
	}

	public int getHierPaths() {
		return hierPaths;
	}

	public int getRelatedPaths() {
		return relatedPaths;
	}

	public int getHierPathLength() {
		return hierPathLength;
	}

	public int getRelatedPathLength() {
		return relatedPathLength;
	}

	public String[] asStringArray() {
		return new String[] { nodeFrom, nodeTo, String.valueOf(hierPaths), String.valueOf(hierPathLength),
				String.valueOf(relatedPaths), String.valueOf(relatedPathLength) };
	}

}
