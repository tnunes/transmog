package org.biosemantics.disambiguation.knowledgebase.validation.impl;

import java.util.Collection;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.validation.NotationInputValidator;
import org.biosemantics.disambiguation.knowledgebase.validation.ValidationUtilityService;

public class NotationInputValidatorImpl implements NotationInputValidator {

	private ValidationUtilityService validationUtilityService;

	public void setValidationUtilityService(ValidationUtilityService validationUtilityService) {
		this.validationUtilityService = validationUtilityService;
	}

	@Override
	public void validateDomain(Domain domain) {
		if (validationUtilityService.isNull(domain))
			throw new NullArgumentException("domain");
	}

	@Override
	public void validateCode(String code) {
		if (validationUtilityService.isBlankString(code))
			throw new IllegalArgumentException("code cannot be blank");
	}

	@Override
	public void validateNotation(Notation notation) {
		if (validationUtilityService.isNull(notation))
			throw new NullArgumentException("notation");
		validateDomain(notation.getDomain());
		validateCode(notation.getCode());
	}

	@Override
	public void validateNotations(Collection<Notation> notations) {
		if (validationUtilityService.isBlankCollection(notations))
			throw new IllegalArgumentException("notations cannot be blank");
		for (Notation notation : notations) {
			validateNotation(notation);
		}

	}

}
