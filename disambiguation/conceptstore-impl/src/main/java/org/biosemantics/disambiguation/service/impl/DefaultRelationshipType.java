package org.biosemantics.disambiguation.service.impl;

import org.neo4j.graphdb.RelationshipType;

public enum DefaultRelationshipType implements RelationshipType {
	LABELS, LABEL, CONCEPTS, CONCEPT, NOTATIONS, NOTATION, HAS_NOTATION, PREDICATES, PREDICATE, CONCEPT_SCHEMES, TOP_CONCEPT, HAS_NOTE, NOTES, NOTE, DOMAINS, HAS_DOMAIN, HAS_LABEL,

}
