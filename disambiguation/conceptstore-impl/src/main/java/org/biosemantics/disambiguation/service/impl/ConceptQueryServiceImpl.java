package org.biosemantics.disambiguation.service.impl;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.service.ConceptQueryService;
import org.biosemantics.disambiguation.service.IndexService;
import org.springframework.beans.factory.annotation.Required;

public class ConceptQueryServiceImpl implements ConceptQueryService {

	private IndexService indexService;

	@Required
	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	@Override
	public Collection<Concept> fullTextSearch(String text, int maxResults) {
		return indexService.fullTextSearch(text, maxResults);
	}

	@Override
	public Concept getConceptByUuid(String uuid) {
		return indexService.getConceptByUuid(uuid);
	}

}
