package org.biosemantics.disambiguation.service;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Notation;

public interface IndexService {

	Collection<Label> getLabelsByText(String text);

	void indexLabel(Label label);

	Collection<Notation> getNotationByCode(String code);

	void indexNotation(Notation notation);

	Concept getConceptByUuid(String uuid);

	void indexConcept(Concept concept);

	Collection<Concept> fullTextSearch(String text, int maxResults);

	void updateFullTextIndex(Concept concept);
}
