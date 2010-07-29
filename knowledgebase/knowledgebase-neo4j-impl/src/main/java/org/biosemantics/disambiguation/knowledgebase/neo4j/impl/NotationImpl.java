package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.api.Domain;
import org.biosemantics.disambiguation.knowledgebase.api.Notation;
import org.neo4j.graphdb.Node;

public class NotationImpl implements Notation  {
private static final String DOMAIN_PROPERTY = "domain";
private static final String CODE_PROPERTY = "code";
	
	private Node underlyingNode;

	public NotationImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public Domain getDomain() {
		return Domain.valueOf((String) underlyingNode.getProperty(DOMAIN_PROPERTY));
	}
	
	public void setDomain(Domain domain){
		if (domain == null)
			throw new NullArgumentException("domain");
		underlyingNode.setProperty(DOMAIN_PROPERTY, domain.name());
	}

	@Override
	public String getCode() {
		return (String) underlyingNode.getProperty(CODE_PROPERTY);
	}
	
	public void setCode(String code){
		if( code == null)
			throw new NullArgumentException("code");
		underlyingNode.setProperty(CODE_PROPERTY, code);
	}
	
	@Override
	public boolean equals(final Object notationImpl) {
		if (notationImpl instanceof NotationImpl) {
			return this.underlyingNode.equals(((NotationImpl) notationImpl).underlyingNode);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.underlyingNode.hashCode();
	}
	
	public NotationImpl withDomain(Domain domain){
		setDomain(domain);
		return this;
	}
	
	public NotationImpl withCode(String code){
		setCode(code);
		return this;
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}
	
	
}
