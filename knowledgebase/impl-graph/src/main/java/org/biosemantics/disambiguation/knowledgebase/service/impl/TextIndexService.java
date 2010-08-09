package org.biosemantics.disambiguation.knowledgebase.service.impl;

import java.util.Collection;

import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.Domain;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Notation;

public interface TextIndexService {

	void indexLabel(Label label);

	void indexConcept(Concept concept);

	void indexNotation(Notation notation);

	Collection<Label> getLabelsByText(String text);

	Label getLabelById(String id);

	Collection<Notation> getNotationsByCode(String code);
	
	Notation getNotationsByDomainAndCode(Domain domain, String code);

	Concept getConceptById(String id);
	
	Collection<Concept> fullTextSearch(String text, int maxResults);
	
	
}
