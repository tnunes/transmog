package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;

public interface ConceptService {
	Concept createConcept(Collection<Label> labels);

	Concept createConcept(Collection<Label> labels, Collection<Notation> notations);

	Concept createConcept(Collection<Label> labels, Collection<Notation> notations, Collection<Note> notes);

	Concept createPredicate(Collection<Label> labels);

	Concept createPredicate(Collection<Label> labels, Collection<Notation> notations);

}
