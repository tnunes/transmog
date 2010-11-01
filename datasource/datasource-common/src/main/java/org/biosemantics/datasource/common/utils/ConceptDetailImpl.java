package org.biosemantics.datasource.common.utils;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.datasource.common.ConceptDetail;

public class ConceptDetailImpl implements ConceptDetail {
	private ConceptType conceptType;
	private Concept concept;

	public ConceptType getConceptType() {
		return conceptType;
	}

	public void setConceptType(ConceptType conceptType) {
		this.conceptType = conceptType;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public ConceptDetailImpl(ConceptType conceptType, Concept concept) {
		super();
		this.conceptType = conceptType;
		this.concept = concept;
	}

}
