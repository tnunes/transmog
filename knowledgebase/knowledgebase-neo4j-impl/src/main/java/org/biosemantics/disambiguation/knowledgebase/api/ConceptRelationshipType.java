package org.biosemantics.disambiguation.knowledgebase.api;

import org.neo4j.graphdb.RelationshipType;

public enum ConceptRelationshipType implements RelationshipType {
	NARROWER, BROADER, RELATED
}
