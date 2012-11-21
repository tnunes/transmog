package org.biosemantics.conceptstore.domain.impl;

import org.neo4j.graphdb.*;

public enum RlspType implements RelationshipType {
	IN_SCHEME, HAS_LABEL, HAS_NOTATION, IS_INVERSE_OF, SAME_AS, SUB_PROP_OF
}
