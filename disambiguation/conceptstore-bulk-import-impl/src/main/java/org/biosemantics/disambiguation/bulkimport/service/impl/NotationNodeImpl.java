package org.biosemantics.disambiguation.bulkimport.service.impl;

public class NotationNodeImpl {
	private String domainUuid;
	private String code;
	private long nodeId;
	public NotationNodeImpl(String domainUuid, String code, long nodeId) {
		super();
		this.domainUuid = domainUuid;
		this.code = code;
		this.nodeId = nodeId;
	}
	public String getDomainUuid() {
		return domainUuid;
	}
	public String getCode() {
		return code;
	}
	public long getNodeId() {
		return nodeId;
	}
	
	

}
