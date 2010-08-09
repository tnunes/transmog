package org.biosemantics.disambiguation.knowledgebase.service.impl;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.neo4j.graphdb.Node;

public class LabelImpl implements Label {
	private static final String ID_PROPERTY = "id";
	private static final String LABEL_TYPE_PROPERTY = "labelType";
	private static final String LANGUAGE_PROPERTY = "language";
	private static final String TEXT_PROPERTY = "text";
	private Node underlyingNode;

	public LabelImpl(Node node) {
		this.underlyingNode = node;
	}

	@Override
	public String getId(){
		return (String)underlyingNode.getProperty(ID_PROPERTY);
	}
	
	public void setId(String id){
		if(id == null)
			throw new NullArgumentException("id");
		underlyingNode.setProperty(ID_PROPERTY, id);
	}
	
	@Override
	public LabelType getLabelType() {
		return LabelType.valueOf((String) underlyingNode.getProperty(LABEL_TYPE_PROPERTY));
	}

	
	public void setLabelType(LabelType labelType) {
		if (labelType == null)
			throw new NullArgumentException("labelType");
		underlyingNode.setProperty(LABEL_TYPE_PROPERTY, labelType.name());
	}

	
	@Override
	public Language getLanguage() {
		return Language.valueOf((String) underlyingNode.getProperty(LANGUAGE_PROPERTY));
	}

	
	public void setLanguage(Language language) {
		if (language == null)
			throw new NullArgumentException("language");
		underlyingNode.setProperty(LANGUAGE_PROPERTY, language.name());
	}

	
	@Override
	public String getText() {
		return (String) underlyingNode.getProperty(TEXT_PROPERTY);
	}

	public void setText(String text) {
		if (StringUtils.isBlank(text))
			throw new IllegalArgumentException("text cannot be blank");
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
	
	public LabelImpl withId(String id){
		setId(id);
		return this;
	}

	public LabelImpl withLabelType(LabelType labelType) {
		setLabelType(labelType);
		return this;
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
