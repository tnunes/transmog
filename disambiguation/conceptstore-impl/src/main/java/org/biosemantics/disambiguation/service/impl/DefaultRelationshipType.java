package org.biosemantics.disambiguation.service.impl;

import org.neo4j.graphdb.RelationshipType;

public enum DefaultRelationshipType implements RelationshipType {
	LABELS, LABEL, CONCEPTS, CONCEPT, NOTATIONS, NOTATION, HAS_NOTATION, PREDICATES, PREDICATE, CONCEPT_SCHEMES, CONCEPT_SCHEME, TOP_CONCEPT, HAS_NOTE, NOTES, NOTE, DOMAINS, DOMAIN, HAS_DOMAIN, HAS_PREFERRED_LABEL, HAS_ALTERNATE_LABEL, HAS_HIDDEN_LABEL

}
