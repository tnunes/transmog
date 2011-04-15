package org.biosemantics.disambiguation.bulkimport.service.impl;

public class LabelNodeImpl {
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
