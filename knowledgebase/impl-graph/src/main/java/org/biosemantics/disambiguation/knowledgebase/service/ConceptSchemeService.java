package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;

public interface ConceptSchemeService {
	Concept createConceptScheme(Collection<Label> labels);
	
	Concept createConceptScheme(Collection<Label> labels, Collection<Notation> notations);

	void addTopConceptsToScheme(String schemeId, Collection<Concept> concepts);
	
	

}
