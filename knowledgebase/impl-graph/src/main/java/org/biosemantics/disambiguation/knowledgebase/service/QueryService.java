package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;

public interface QueryService {
	Collection<Concept> getConceptsByNotation(Domain domain, String code);
	Concept getConceptById(String id);
	Collection<Concept> getConceptsByLabelText(String labelText);
	Collection<Concept> getConceptsByFullTextSearch(String text, int maxResults);
	
}
