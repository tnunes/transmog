package org.biosemantics.disambiguation.service.impl;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.common.service.extn.ConceptStorageServiceDecorator;
import org.biosemantics.disambiguation.service.IndexService;

public class ConceptStorageServiceIndexingImpl extends ConceptStorageServiceDecorator {

	private IndexService indexService;

	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	public ConceptStorageServiceIndexingImpl(ConceptStorageService conceptStorageService) {
		super(conceptStorageService);
	}


	@Override
	public Concept createConcept(ConceptType conceptType, Concept concept) {
		Concept createdConcept = conceptStorageService.createConcept(conceptType, concept);
		indexService.indexConcept(createdConcept);
		return createdConcept;
	}
}
