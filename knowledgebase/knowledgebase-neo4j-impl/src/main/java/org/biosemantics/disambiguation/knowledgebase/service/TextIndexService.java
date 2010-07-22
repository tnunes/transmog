package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;

public interface TextIndexService {

	void indexLabel(Label label);

	void indexConcept(Concept concept);

	void indexNotation(Notation notation);

	Collection<Label> getLabelsByText(String text);

	Label getLabelById(String id);

	Collection<Notation> getNotationByCode(String code);

	Concept getConceptById(String id);
	
	Collection<Concept> fullTextSearch(String text, int maxResults);
}
