package org.biosemantics.eviped.web.service;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.biosemantics.eviped.tools.service.SearchQueryResult;
import org.biosemantics.eviped.web.model.Search;

public class SearchResultProvider extends SortableDataProvider<SearchQueryResult> {

	@SpringBean
	private SearchService searchService;
	private Collection<SearchQueryResult> searchQueryResults;
	
	public SearchResultProvider(Search search) throws JAXBException{
		this.searchQueryResults = searchService.performSearch(search);
	}
	
	public Iterator<? extends SearchQueryResult> iterator(int arg0, int arg1) {
		return searchQueryResults.iterator();
	}

	public IModel<SearchQueryResult> model(SearchQueryResult searchQueryResult) {
		return new DetachableContactModel(searchQueryResult);
	}

	public int size() {
		return searchQueryResults.size();
	}

}
