package org.biosemantics.disambiguation.knowledgebase.service;

import org.neo4j.graphdb.RelationshipType;

public enum KnowledgebaseRelationshipType implements RelationshipType {
	LABELS, LABEL, CONCEPTS, CONCEPT, HAS_LABEL, NOTATIONS, NOTATION, HAS_NOTATION, PREDICATES, PREDICATE, CONCEPT_SCHEMES, CONCEPT_SCHEME, TOP_CONCEPT, HAS_NOTE, NOTES, NOTE

}
