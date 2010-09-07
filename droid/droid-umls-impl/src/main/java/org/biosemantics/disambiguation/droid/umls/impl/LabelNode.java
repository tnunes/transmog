package org.biosemantics.disambiguation.droid.umls.impl;

import java.io.Serializable;

public class LabelNode implements Serializable {

	private static final long serialVersionUID = -3363110511328812716L;
	private String sui;
	private long nodeId;

	public String getSui() {
		return sui;
	}

	public void setSui(String sui) {
		this.sui = sui;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public LabelNode(String sui,long nodeId) {
		super();
		this.sui = sui;
		this.nodeId = nodeId;
	}

	public LabelNode() {
		super();
		// TODO Auto-generated constructor stub
	}

}
