package org.biosemantics.eviped.web.ui;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.biosemantics.eviped.web.model.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchForm extends Form {
	private static final Logger logger = LoggerFactory.getLogger(SearchForm.class);

	public SearchForm(String id) {
		super(id, new CompoundPropertyModel(new Search()));
		add(new TextField("searchTxt").setRequired(true));
	}

	protected void onSubmit() {
		Search search = (Search) getModelObject();
		logger.debug("submit called on search form with txt = {}", search.getSearchTxt());
		 setResponsePage(new SearchResultPage(search));
	}
}
