package org.biosemantics.disambiguation.manager.controller;

import static com.google.common.base.Preconditions.checkArgument;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.service.ConceptQueryService;
import org.biosemantics.disambiguation.manager.adapter.ConceptAdapter;
import org.biosemantics.disambiguation.manager.common.MessageConstants;
import org.biosemantics.disambiguation.manager.common.MessageManager;
import org.biosemantics.disambiguation.manager.dto.ConceptResult;
import org.biosemantics.disambiguation.manager.dto.ConceptResultResponse;
import org.biosemantics.disambiguation.manager.dto.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConceptController {
	private static final Logger logger = LoggerFactory.getLogger(ConceptController.class);

	private ConceptQueryService conceptQueryService;
	private ConceptAdapter conceptAdapter;
	private MessageManager messageManager;

	@Required
	public void setConceptQueryService(ConceptQueryService conceptQueryService) {
		this.conceptQueryService = conceptQueryService;
	}

	@Required
	public void setConceptAdapter(ConceptAdapter conceptAdapter) {
		this.conceptAdapter = conceptAdapter;
	}

	@Required
	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	@RequestMapping(value = "/concept/{uuid}", method = RequestMethod.GET)
	public ModelAndView getConcept(@PathVariable("uuid") String uuid) {
		checkArgument(!uuid.isEmpty());
		long start = System.currentTimeMillis();
		Concept concept = conceptQueryService.getConceptByUuid(uuid);
		ConceptResultResponse conceptResultResponse;
		if (concept == null) {
			logger.warn("No concept found for UUID " + uuid);
			conceptResultResponse = new ConceptResultResponse(ResponseType.WARNING, messageManager.getMessage(
					MessageConstants.NO_RESULTS, new Object[] { uuid }));
		} else {
			ConceptResult conceptResult = conceptAdapter.adapt(concept);
			conceptResultResponse = new ConceptResultResponse(conceptResult, System.currentTimeMillis() - start);
		}
		ModelAndView mav = new ModelAndView();
		mav.setViewName("concept");
		mav.addObject("conceptResultResponse", conceptResultResponse);
		long end = System.currentTimeMillis();
		logger.info("time taken {}(ms)", end - start);
		return mav;
	}

}
