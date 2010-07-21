package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;


public interface TextIndexService {
	
	void indexLabelText(Label label);
	
	Collection<Label> getLabelsByText(String text);

	Label getLabelById(String id);

	void indexNotation(Notation notation);
	
	void indexConcept(Concept concept);
}
