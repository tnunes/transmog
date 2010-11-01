package org.biosemantics.disambiguation.domain.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.utils.domain.impl.ErrorMessage;
import org.neo4j.graphdb.Node;

public class LabelImpl implements Label {

	private static final long serialVersionUID = 2625024353276251601L;
	private static final String LANGUAGE_PROPERTY = "language";
	private static final String TEXT_PROPERTY = "text";
	private Node underlyingNode;

	public LabelImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public Language getLanguage() {
		return Language.valueOf((String) underlyingNode.getProperty(LANGUAGE_PROPERTY));
	}

	public void setLanguage(Language language) {
		checkNotNull(language);
		underlyingNode.setProperty(LANGUAGE_PROPERTY, language.name());
	}

	@Override
	public String getText() {
		return (String) underlyingNode.getProperty(TEXT_PROPERTY);
	}

	public void setText(String text) {
		checkNotNull(text);
		checkArgument(!text.isEmpty(), ErrorMessage.EMPTY_STRING_MSG, text);
		underlyingNode.setProperty(TEXT_PROPERTY, text);
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

	public LabelImpl withLanguage(Language language) {
		setLanguage(language);
		return this;
	}

	public LabelImpl withText(String text) {
		setText(text);
		return this;
	}
}
