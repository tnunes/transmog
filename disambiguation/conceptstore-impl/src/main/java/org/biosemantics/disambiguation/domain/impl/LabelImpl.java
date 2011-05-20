package org.biosemantics.disambiguation.domain.impl;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Source;
import org.neo4j.graphdb.Node;

public class LabelImpl implements Label {

	private static final long serialVersionUID = 2625024353276251601L;
	public static final String LANGUAGE_PROPERTY = "language";
	public static final String TEXT_PROPERTY = "text";
	private Node underlyingNode;

	public LabelImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public Language getLanguage() {
		return LanguageImpl.valueOf((String) underlyingNode.getProperty(LANGUAGE_PROPERTY));
	}

	@Override
	public String getText() {
		return (String) underlyingNode.getProperty(TEXT_PROPERTY);
	}

	@Override
	public long getId() {
		return underlyingNode.getId();
	}

	@Override
	public Source[] getSources() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	@Override
	public boolean equals(final Object labelImpl) {
		if (labelImpl instanceof LabelImpl) {
			return this.underlyingNode.equals(((LabelImpl) labelImpl).getUnderlyingNode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.underlyingNode.hashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
