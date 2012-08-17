package org.biosemantics.eviped.web.ui;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Link;
import java.util.Collection;

import org.biosemantics.eviped.tools.service.QueryResult;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchResultPanel extends VerticalLayout {

	private Table searchResultsTable = new Table("Search Results");
	private static final Logger logger = LoggerFactory.getLogger(SearchResultPanel.class);

	private SearchResultPanel() {
		searchResultsTable.addContainerProperty("pubmedId", Integer.class, null);
		searchResultsTable.addContainerProperty("weight", Integer.class, null);
		searchResultsTable.addContainerProperty("pubmedLink", Link.class, null);
		searchResultsTable.setPageLength(0);
		searchResultsTable.setColumnHeader("pubmedId", "Pubmed Id");
		searchResultsTable.setColumnHeader("weight", "Confidence");
		searchResultsTable.setColumnHeader("pubmedLink", "Abstract");
		this.addComponent(searchResultsTable);
	}

	public static SearchResultPanel getInstance() {
		return new SearchResultPanel();
	}

	public void showResults(Collection<QueryResult> searchQueryResults) {
		int ctr = 0;
		for (QueryResult searchQueryResult : searchQueryResults) {
			//logger.info("{}", searchQueryResult);
			Link link = new Link("Goto Abstract", new ExternalResource("http://www.ncbi.nlm.nih.gov/pubmed?term=" + searchQueryResult.getPmid()));
			link.setTargetName("_blank");
			searchResultsTable.addItem(new Object[]{searchQueryResult.getPmid(), searchQueryResult.getWeight(), link}, ++ctr);
		}

	}
}
