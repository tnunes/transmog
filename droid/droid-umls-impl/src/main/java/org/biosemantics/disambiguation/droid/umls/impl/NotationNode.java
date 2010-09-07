package org.biosemantics.disambiguation.droid.umls.impl;

import java.io.Serializable;

public class NotationNode implements Serializable {

	private static final long serialVersionUID = 7984957304299019649L;
	private String domain;
	private String code;
	private long nodeId;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public NotationNode(String domain, String code, long nodeId) {
		super();
		this.domain = domain;
		this.code = code;
		this.nodeId = nodeId;
	}

	public NotationNode() {
		super();
		// TODO Auto-generated constructor stub
	}

}
