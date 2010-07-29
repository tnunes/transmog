package org.biosemantics.disambiguation.knowledgebase.api;

import java.util.Collection;

public interface ConceptFactory {
	Concept createConcept(Collection<Label> labels);
	Concept createConcept(Collection<Label> labels, Collection<Notation> notations);
}
