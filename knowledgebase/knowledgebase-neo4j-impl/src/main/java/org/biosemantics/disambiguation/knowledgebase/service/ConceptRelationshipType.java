package org.biosemantics.disambiguation.knowledgebase.service;

import org.neo4j.graphdb.RelationshipType;

public enum ConceptRelationshipType implements RelationshipType {
	NARROWER, BROADER, RELATED, CLOSE_MATCH, EXACT_MATCH;
}
