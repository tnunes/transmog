package org.biosemantics.datasource.common;

import org.biosemantics.conceptstore.common.domain.Relationship;

public interface DataSourceWriter {
	boolean writeConcept(ConceptDetail conceptDetail);
	
	boolean writeRelationship(Relationship relationship);
	
}
