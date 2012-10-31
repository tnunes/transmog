package org.biosemantics.eviped.web.service;

import java.util.Collection;

import javax.xml.bind.JAXBException;

import org.biosemantics.eviped.tools.service.QueryBuilder;
import org.biosemantics.eviped.tools.service.SearchQueryResult;
import org.biosemantics.eviped.web.model.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private QueryBuilder queryBuilder;

	public Collection<SearchQueryResult> performSearch(Search search) throws JAXBException {
		final String drugName = search.getSearchTxt();
		Collection<SearchQueryResult> searchQueryResults = queryBuilder.searchMedline(drugName);
		return searchQueryResults;
	}

}
