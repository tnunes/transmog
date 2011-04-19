package org.biosemantics.disambiguation.bulkimport.service;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptRelationship;
import org.biosemantics.conceptstore.common.domain.ConceptType;

public interface BulkImportService {
	String createConcept(ConceptType conceptType, Concept concept);

	String addRelationship(ConceptRelationship conceptRelationship);
}
