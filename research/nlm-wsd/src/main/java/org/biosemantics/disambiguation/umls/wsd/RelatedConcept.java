package org.biosemantics.disambiguation.umls.wsd;

import java.util.Map;

import org.biosemantics.conceptstore.common.domain.ConceptRelationship;

public class RelatedConcept {

	private String cui;
	private Map<String, ConceptRelationship> cuiConceptRelationshipMap;

	public String getCui() {
		return cui;
	}

	public Map<String, ConceptRelationship> getCuiConceptRelationshipMap() {
		return cuiConceptRelationshipMap;
	}

	public RelatedConcept(String cui, Map<String, ConceptRelationship> cuiConceptRelationshipMap) {
		super();
		this.cui = cui;
		this.cuiConceptRelationshipMap = cuiConceptRelationshipMap;
	}

}
