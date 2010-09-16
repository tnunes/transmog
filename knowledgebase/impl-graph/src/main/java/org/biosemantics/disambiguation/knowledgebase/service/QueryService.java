package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;

public interface QueryService {
	Concept getConceptById(String id);
	Collection<Concept> getConceptsByLabelText(String labelText);
	Collection<Concept> getConceptsByNotationCode(String notationCode);
	Collection<Concept> getConceptsByFullTextSearch(String text, int maxResults);
	
}
