package org.biosemantics.disambiguation.knowledgebase.service;

import org.neo4j.graphdb.RelationshipType;

public enum ConceptRelationshipType implements RelationshipType {
	HAS_NARROWER_CONCEPT, HAS_BROADER_CONCEPT, RELATED, CLOSE_MATCH, EXACT_MATCH;
}
