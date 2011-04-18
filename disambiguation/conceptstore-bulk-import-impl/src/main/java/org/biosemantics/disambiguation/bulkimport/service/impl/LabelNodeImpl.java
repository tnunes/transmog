package org.biosemantics.disambiguation.bulkimport.service.impl;

import java.io.Serializable;

public class LabelNodeImpl implements Serializable {
	private static final long serialVersionUID = -6748858115454687393L;
	private String text;
	private String language;
	private long nodeId;

	public String getText() {
		return text;
	}

	public String getLanguage() {
		return language;
	}

	public long getNodeId() {
		return nodeId;
	}

	public LabelNodeImpl(String text, String language, long nodeId) {
		super();
		this.text = text;
		this.language = language;
		this.nodeId = nodeId;
	}

}
