package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.Collection;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;
import org.biosemantics.disambiguation.knowledgebase.service.NotationInputValidator;

public class NotationInputValidatorImpl  implements NotationInputValidator{

	@Override
	public void validateDomain(Domain domain) {
		if(domain == null)
			throw new NullArgumentException("domain");
	}

	@Override
	public void validateText(String text) {
		if (StringUtils.isEmpty(text))
			throw new IllegalArgumentException("text cannot be blank");
	}

	@Override
	public void validateNotation(Notation notation) {
		if(notation == null)
			throw new NullArgumentException("notation");
		validateDomain(notation.getDomain());
		validateText(notation.getText());
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
