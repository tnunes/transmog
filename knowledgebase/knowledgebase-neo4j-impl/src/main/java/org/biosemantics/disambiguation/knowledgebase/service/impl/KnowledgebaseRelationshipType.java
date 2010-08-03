package org.biosemantics.disambiguation.knowledgebase.neo4j.impl;

import org.neo4j.graphdb.RelationshipType;

public enum KnowledgebaseRelationshipType implements RelationshipType{
	LABELS, LABEL, CONCEPTS, CONCEPT, HAS_LABEL, NOTATIONS, NOTATION, HAS_NOTATION

}
