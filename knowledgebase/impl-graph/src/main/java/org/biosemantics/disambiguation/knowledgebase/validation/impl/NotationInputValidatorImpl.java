package org.biosemantics.disambiguation.knowledgebase.validation.impl;

import java.util.Collection;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.validation.NotationInputValidator;

public class NotationInputValidatorImpl  implements NotationInputValidator{

	@Override
	public void validateDomain(Domain domain) {
		if(domain == null)
			throw new NullArgumentException("domain");
	}

	@Override
	public void validateCode(String code) {
		if (StringUtils.isEmpty(code))
			throw new IllegalArgumentException("code cannot be blank");
	}

	@Override
	public void validateNotation(Notation notation) {
		if(notation == null)
			throw new NullArgumentException("notation");
		validateDomain(notation.getDomain());
		validateCode(notation.getCode());
	}

	@Override
	public void validateNotations(Collection<Notation> notations) {
		if(notations == null)
			throw new NullArgumentException("notations");
		for (Notation notation : notations) {
			validateNotation(notation);
		}
		
	}

}
