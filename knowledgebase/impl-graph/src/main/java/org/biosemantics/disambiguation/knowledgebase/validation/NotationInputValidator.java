package org.biosemantics.disambiguation.knowledgebase.validation;

import java.util.Collection;

import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;

public interface NotationInputValidator {
	
	void validateDomain(Domain domain);
	void validateCode(String code);
	void validateNotation(Notation notation);
	void validateNotations(Collection<Notation> notations);

}
