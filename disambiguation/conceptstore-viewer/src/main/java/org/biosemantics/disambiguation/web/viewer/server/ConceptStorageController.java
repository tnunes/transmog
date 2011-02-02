package org.biosemantics.disambiguation.web.viewer.server;

import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ConceptStorageController {

	private static final Logger logger = LoggerFactory.getLogger(ConceptStorageController.class);
	private ConceptStorageService conceptStorageService;

	@Required
	public void setConceptStorageService(ConceptStorageService conceptStorageService) {
		this.conceptStorageService = conceptStorageService;
	}

	private void init() {
		logger.warn("------> {}", conceptStorageService.toString());

	}

}
