package org.biosemantics.disambiguation.service.impl;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.biosemantics.conceptstore.common.service.extn.ConceptStorageServiceDecorator;
import org.biosemantics.disambiguation.service.IndexService;
import org.springframework.transaction.annotation.Transactional;

public class ConceptStorageServiceIndexingImpl extends ConceptStorageServiceDecorator {

	private IndexService indexService;

	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	public ConceptStorageServiceIndexingImpl(ConceptStorageService conceptStorageService) {
		super(conceptStorageService);
	}

	@Override
	@Transactional
	public Concept createConcept(Concept concept) {
		Concept createdConcept = conceptStorageService.createConcept(concept);
		indexConcept(createdConcept);
		return createdConcept;

	}

	@Override
	@Transactional
	public Concept createPredicate(Concept predicate) {
		Concept createdConcept = conceptStorageService.createPredicate(predicate);
		indexConcept(createdConcept);
		return createdConcept;

	}

	@Override
	@Transactional
	public Concept createConceptScheme(Concept conceptScheme) {
		Concept createdConcept = conceptStorageService.createConceptScheme(conceptScheme);
		indexConcept(createdConcept);
		return createdConcept;
	}

	@Override
	@Transactional
	public Concept createDomain(Concept domain) {
		Concept createdConcept = conceptStorageService.createDomain(domain);
		indexConcept(createdConcept);
		return createdConcept;
	}

	private void indexConcept(Concept concept) {
		indexService.indexConcept(concept);
	}
}
