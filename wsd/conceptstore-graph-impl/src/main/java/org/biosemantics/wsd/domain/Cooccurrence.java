package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

@RelationshipEntity(type = "COOCCURRENCE", useShortNames=true)
public class Cooccurrence extends Rlsp {

	public Cooccurrence(Concept concept, Concept otherConcept, int strength, String predicate, String source) {
		super(concept, otherConcept, strength, predicate, source);
	}
	
	public Cooccurrence() {
		super();
		// TODO Auto-generated constructor stub
	}

}
