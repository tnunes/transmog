package org.biosemantics.eviped.web.service;

import java.util.Collection;

import javax.xml.bind.JAXBException;

import org.biosemantics.eviped.tools.service.SearchQueryResult;
import org.biosemantics.eviped.web.model.Search;

public interface SearchService {

	Collection<SearchQueryResult> performSearch(Search search) throws JAXBException;

}
