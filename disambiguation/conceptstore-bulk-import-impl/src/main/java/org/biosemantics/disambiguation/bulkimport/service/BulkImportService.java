package org.biosemantics.disambiguation.bulkimport.service;

import java.util.List;

import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptRelationship;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Notation;

public interface BulkImportService {

	
	long createRelationship(ConceptRelationship conceptRelationship);

	boolean relationshipExists(ConceptRelationship conceptRelationship);

	long validateAndCreateRelationship(final ConceptRelationship conceptRelationship);

	long createLabel(Label label);

	long createUmlsConcept(ConceptType conceptType, List<ConceptLabel> conceptLabelIds, List<Long> notations,
			String fullText);

	long createNotation(Notation notation);
}
