package org.biosemantics.datasource.common;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;

public interface ConceptDetail {
	
	ConceptType getConceptType();
	Concept getConcept();

}
