package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;

public interface NotationInputValidator {
	
	void validateDomain(Domain domain);
	void validateText(String text);
	void validateNotation(Notation notation);
	void validateNotations(Collection<Notation> notations);

}
