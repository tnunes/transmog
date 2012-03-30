package org.biosemantics.wsd.domain;

import org.springframework.data.neo4j.annotation.RelationshipEntity;

@RelationshipEntity(type = "CHILD", useShortNames=true)
public class Child extends Rlsp{
	
	public Child(Concept concept, Concept otherConcept, int strength, String predicate, String source) {
		super(concept, otherConcept, strength, predicate, source);
	}

	public Child() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
}
