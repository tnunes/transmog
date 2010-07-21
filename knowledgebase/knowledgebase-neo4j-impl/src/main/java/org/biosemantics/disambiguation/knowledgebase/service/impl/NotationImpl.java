package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.neo4j.graphdb.Node;

public class NotationImpl implements Notation  {
private static final String DOMAIN_PROPERTY = "domain";
private static final String TEXT_PROPERTY = "text";
	
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
	public String getText() {
		return (String) underlyingNode.getProperty(TEXT_PROPERTY);
	}
	
	public void setText(String text){
		if( text == null)
			throw new NullArgumentException("text");
		underlyingNode.setProperty(TEXT_PROPERTY, text);
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
	
	public NotationImpl withText(String text){
		setText(text);
		return this;
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}
	
	
}
