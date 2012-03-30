package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

@RelationshipEntity(type = "IN_SCHEME")
public class InScheme extends Rlsp {

	public InScheme(Concept concept, Concept scheme, int strength, String predicate, String source) {
		super(concept, scheme, strength, predicate, source);
	}

	public InScheme() {
		super();
		// TODO Auto-generated constructor stub
	}

}
