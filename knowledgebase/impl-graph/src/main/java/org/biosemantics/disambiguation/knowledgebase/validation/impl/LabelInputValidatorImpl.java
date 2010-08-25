package org.biosemantics.disambiguation.knowledgebase.validation.impl;

import java.util.Collection;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Language;
import org.biosemantics.disambiguation.knowledgebase.validation.LabelInputValidator;
import org.biosemantics.disambiguation.knowledgebase.validation.ValidationUtilityService;

public class LabelInputValidatorImpl implements LabelInputValidator {

	private ValidationUtilityService validationUtilityService;

	public void setValidationUtilityService(ValidationUtilityService validationUtilityService) {
		this.validationUtilityService = validationUtilityService;
	}

	@Override
	public void validateLabels(Collection<Label> labels) {
		if (validationUtilityService.isBlankCollection(labels))
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
		if (validationUtilityService.isBlankString(text))
			throw new IllegalArgumentException("label text cannot be blank");

	}

	@Override
	public void validateLanguage(Language language) {
		if (validationUtilityService.isNull(language))
			throw new NullArgumentException("language");
	}

}
