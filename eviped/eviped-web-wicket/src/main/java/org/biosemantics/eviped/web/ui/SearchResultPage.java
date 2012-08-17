package org.biosemantics.eviped.web.ui;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.PropertyPopulator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.biosemantics.eviped.tools.service.SearchQueryResult;
import org.biosemantics.eviped.web.model.Search;
import org.biosemantics.eviped.web.service.SearchResultProvider;
import org.biosemantics.eviped.web.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchResultPage extends WebPage {

	private static final Logger logger = LoggerFactory.getLogger(SearchResultPage.class);

	@SpringBean
	private SearchService searchService;

//	public SearchResultPage(Search search) {
//		try {
//			Collection<SearchQueryResult> searchQueryResults = searchService.performSearch(search);
//			logger.debug("{}", searchQueryResults);
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public SearchResultPage(Search search) throws JAXBException {
		 final SearchResultProvider searchResultProvider = new SearchResultProvider(search);
		 List<ICellPopulator<SearchQueryResult>> columns = new ArrayList<ICellPopulator<SearchQueryResult>>();
	        columns.add(new PropertyPopulator<SearchQueryResult>("pmid"));
	        columns.add(new PropertyPopulator<SearchQueryResult>("weight"));
	        add(new DataGridView<SearchQueryResult>("rows", columns, new SearchResultProvider(search)));
	}

}
