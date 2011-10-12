package org.biosemantics.disambiguation.script;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;

public interface ConceptTypeScript {
	Collection<Concept> getConceptsByType(ConceptType conceptType);
}
