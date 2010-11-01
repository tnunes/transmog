package org.biosemantics.datasource.common;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Relationship;

public interface DataSourceWriter {
	boolean writeConcept(Concept concept);
	boolean writePredicate(Concept concept);
	boolean writeConceptScheme(Concept concept);
	boolean writeDomain(Concept concept);
	boolean writeRelationship(Relationship relationship);
	
}
