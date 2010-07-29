package org.biosemantics.disambiguation.knowledgebase.api;

import java.util.Collection;

public interface NotationInputValidator {
	
	void validateDomain(Domain domain);
	void validateCode(String code);
	void validateNotation(Notation notation);
	void validateNotations(Collection<Notation> notations);

}
