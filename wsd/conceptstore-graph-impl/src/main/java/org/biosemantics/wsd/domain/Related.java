package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

@RelationshipEntity(type = "RELATED")
public class Related extends Rlsp {

	public Related(Concept concept, Concept otherConcept, int strength, String predicate, String source) {
		super(concept, otherConcept, strength, predicate, source);
	}
	
	public Related() {
		super();
		// TODO Auto-generated constructor stub
	}

}
