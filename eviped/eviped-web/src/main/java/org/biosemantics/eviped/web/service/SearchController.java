package org.biosemantics.eviped.web.service;
import java.util.Collection;

import javax.xml.bind.JAXBException;
import org.biosemantics.eviped.tools.service.Article;

import org.biosemantics.eviped.tools.service.QueryBuilder;
import org.biosemantics.eviped.web.ui.BodyPanel;
import org.biosemantics.eviped.web.ui.NorthPanel;
import org.biosemantics.eviped.web.ui.SearchResultPanel;

public class SearchController {

	private NorthPanel northPanel;
	private QueryBuilder queryBuilder;
	private BodyPanel bodyPanel;

	public SearchController(NorthPanel northPanel, BodyPanel bodyPanel, QueryBuilder queryBuilder) {
		this.northPanel = northPanel;
		this.bodyPanel = bodyPanel;
		this.queryBuilder = queryBuilder;
	}

	public void search() {
		String searchText = northPanel.getSearchText();
		try {
			Collection<Article> articles = queryBuilder.getArticles(searchText);
			
			SearchResultPanel searchResultPanel = SearchResultPanel.getInstance();
			searchResultPanel.showResults(articles);
			bodyPanel.removeAllComponents();
			bodyPanel.addComponent(searchResultPanel);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
