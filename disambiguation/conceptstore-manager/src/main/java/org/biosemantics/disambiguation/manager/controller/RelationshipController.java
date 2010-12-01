package org.biosemantics.disambiguation.manager.controller;

import static com.google.common.base.Preconditions.*;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.conceptstore.common.service.ConceptQueryService;
import org.biosemantics.conceptstore.common.service.RelationshipStoreService;
import org.biosemantics.disambiguation.manager.common.MessageConstants;
import org.biosemantics.disambiguation.manager.common.MessageManager;
import org.biosemantics.disambiguation.manager.dto.ConceptResult;
import org.biosemantics.disambiguation.manager.dto.ConceptResultResponse;
import org.biosemantics.disambiguation.manager.dto.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


public class RelationshipController {
	private static final Logger logger = LoggerFactory.getLogger(QueryController.class);
	private RelationshipStoreService relationshipStoreService;
	private ConceptQueryService conceptQueryService;
	private MessageManager messageManager;

	@Required
	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = checkNotNull(messageManager);
	}

	@Required
	public void setRelationshipStoreService(RelationshipStoreService relationshipStoreService) {
		this.relationshipStoreService = relationshipStoreService;
	}

	@Required
	public void setConceptQueryService(ConceptQueryService conceptQueryService) {
		this.conceptQueryService = conceptQueryService;
	}

	@Transactional
	@RequestMapping(value = "/relationship/{uuid}", method = RequestMethod.GET)
	public ModelAndView getConcept(@PathVariable("uuid") String uuid) {
		checkArgument(!uuid.isEmpty());
		long start = System.currentTimeMillis();
		Concept concept = conceptQueryService.getConceptByUuid(uuid);
		if (concept == null) {
			logger.warn("No concept found for uuid {}, returning empty relationships " + uuid);
		} else {
			Collection<Relationship> relationships = relationshipStoreService.getAllRelationshipsForConcept(uuid);
			if (CollectionUtils.isEmpty(relationships)) {
				logger.warn("No relationship found for uuid {} " + uuid);
			} else {
				// ConceptResult conceptResult = conceptAdapter.adapt(concept);
				// conceptResultResponse = new ConceptResultResponse(conceptResult, System.currentTimeMillis() - start);
			}
		}
		ModelAndView mav = new ModelAndView();
		mav.setViewName("concept");
		// mav.addObject("conceptResultResponse", conceptResultResponse);
		long end = System.currentTimeMillis();
		logger.info("time taken {}(ms)", end - start);
		return mav;
	}
}
