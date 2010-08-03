package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import java.util.Collection;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.api.Label;
import org.biosemantics.disambiguation.knowledgebase.api.LabelInputValidator;
import org.biosemantics.disambiguation.knowledgebase.api.Language;

public class LabelInputValidatorImpl implements LabelInputValidator{

	@Override
	public void validateLabels(Collection<Label> labels) {
		if(labels == null || labels.isEmpty())
			throw new IllegalArgumentException("labels cannot be null or empty");
		for (Label label : labels) {
			validateLabel(label);
		}
	}
	
	@Override
	public void validateLabel(Label label) {
		validateLanguage(label.getLanguage());
		validateText(label.getText());
	}

	@Override
	public void validateText(String text) {
		if (StringUtils.isBlank(text))
			throw new IllegalArgumentException("label text cannot be blank");
		
	}

	@Override
	public void validateLanguage(Language language) {
		if (language == null)
			throw new NullArgumentException("language");
	}

	

}
