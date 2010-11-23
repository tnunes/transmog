package org.biosemantics.disambiguation.manager.controller;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.service.ConceptQueryService;
import org.biosemantics.disambiguation.manager.adapter.QueryResultAdapter;
import org.biosemantics.disambiguation.manager.common.MessageConstants;
import org.biosemantics.disambiguation.manager.common.MessageManager;
import org.biosemantics.disambiguation.manager.common.UserPreference;
import org.biosemantics.disambiguation.manager.dto.QueryResult;
import org.biosemantics.disambiguation.manager.dto.QueryResultResponse;
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

@Controller
public class QueryController {
	private static final Logger logger = LoggerFactory.getLogger(QueryController.class);
	private static final int MAX_RESULTS = 50;
	private ConceptQueryService conceptQueryService;
	private QueryResultAdapter queryResultAdapter;
	private MessageManager messageManager;
	private UserPreference userPreference;
	private static final String LABEL_PREFIX = "lbl:";
	private static final String NOTATION_PREFIX = "not:";

	@Required
	public void setConceptQueryService(ConceptQueryService conceptQueryService) {
		this.conceptQueryService = conceptQueryService;
	}

	@Required
	public void setQueryResultAdapter(QueryResultAdapter queryResultAdapter) {
		this.queryResultAdapter = checkNotNull(queryResultAdapter);
	}

	@Required
	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = checkNotNull(messageManager);
	}

	@Required
	public void setUserPreference(UserPreference userPreference) {
		this.userPreference = checkNotNull(userPreference);
	}

	@Transactional
	@RequestMapping(value = "/query/{txt}", method = RequestMethod.GET)
	public ModelAndView getQueryResults(@PathVariable("txt") String searchText) {
		checkArgument(!searchText.isEmpty());
		long start = System.currentTimeMillis();
		logger.info("getQueryResults invoked with \"{}\" queryTxt ", searchText);
		Collection<Concept> concepts = null;
		if (searchText.startsWith(LABEL_PREFIX)) {
			searchText = searchText.substring(LABEL_PREFIX.length(), searchText.length());
			concepts = conceptQueryService.getConceptsByLabelText(searchText);
		} else if (searchText.startsWith(NOTATION_PREFIX)) {
			searchText = searchText.substring(NOTATION_PREFIX.length(), searchText.length());
			concepts = conceptQueryService.getConceptsByNotationCode(searchText);
		} else {
			
			concepts = conceptQueryService.fullTextSearch(searchText, MAX_RESULTS);
		}
		QueryResultResponse queryResultResponse = null;
		if (CollectionUtils.isEmpty(concepts)) {
			logger.warn("No search results found for text \"{}\"", searchText);
			queryResultResponse = new QueryResultResponse(ResponseType.WARNING, messageManager.getMessage(
					MessageConstants.NO_RESULTS, new Object[] { searchText }));
		} else {
			List<QueryResult> queryResults = new ArrayList<QueryResult>(concepts.size());
			for (Concept concept : concepts) {

				if (CollectionUtils.isEmpty(concept.getLabels())) {
					logger.warn("no preferred label for concept with uuid {} ", concept.getUuid());
				} else {
					queryResults.add(queryResultAdapter.adapt(concept));
				}
			}
			queryResultResponse = new QueryResultResponse(queryResults, System.currentTimeMillis() - start);
		}
		queryResultResponse.setSearchText(searchText);
		queryResultResponse.setPreviousSearchTexts(userPreference.getPreviousSearchTexts());

		userPreference.addSearchText(searchText);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		mav.addObject("queryResultResponse", queryResultResponse);
		long end = System.currentTimeMillis();
		logger.info("time taken {}(ms)", end - start);
		return mav;
	}
	
	@Transactional
	@RequestMapping(value = "/conceptType/{txt}", method = RequestMethod.GET)
	public ModelAndView getConceptsByType(@PathVariable("txt") String text) {
		long start = System.currentTimeMillis();
		ConceptType conceptType = ConceptType.valueOf(text);
		Collection<Concept> concepts = conceptQueryService.getConceptsByType(conceptType);
		QueryResultResponse queryResultResponse = null;
		if (CollectionUtils.isEmpty(concepts)) {
			logger.warn("No search results found for text \"{}\"", text);
			queryResultResponse = new QueryResultResponse(ResponseType.WARNING, messageManager.getMessage(
					MessageConstants.NO_RESULTS, new Object[] { text }));
		} else {
			List<QueryResult> queryResults = new ArrayList<QueryResult>(concepts.size());
			for (Concept concept : concepts) {

				if (CollectionUtils.isEmpty(concept.getLabels())) {
					logger.warn("no preferred label for concept with uuid {} ", concept.getUuid());
				} else {
					queryResults.add(queryResultAdapter.adapt(concept));
				}
			}
			queryResultResponse = new QueryResultResponse(queryResults, System.currentTimeMillis() - start);
		}
		queryResultResponse.setSearchText(text);
		queryResultResponse.setPreviousSearchTexts(userPreference.getPreviousSearchTexts());

		userPreference.addSearchText(text);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		mav.addObject("queryResultResponse", queryResultResponse);
		long end = System.currentTimeMillis();
		logger.info("time taken {}(ms)", end - start);
		return mav;
	}
}
