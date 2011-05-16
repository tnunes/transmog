package org.biosemantics.disambiguation.conceptstore.web.widget;

import com.vaadin.ui.Tree;

public class NavigationTree extends Tree {

	public static final Object SEARCH_HISTORY = WidgetConstants.TREE_SEARCH_HISTORY;
	public static final Object CONCEPT_HISTORY = WidgetConstants.TREE_CONCEPT_HISTORY;
	public static final Object FAVORITE = WidgetConstants.TREE_FAVORITE;

	public NavigationTree() {

		addItem(SEARCH_HISTORY);
		addItem(CONCEPT_HISTORY);
		addItem(FAVORITE);
		/*
		 * We want items to be selectable but do not want the user to be able to
		 * de-select an item.
		 */
		setSelectable(true);
		setNullSelectionAllowed(false);
	}

	public void addToSearchHistory(String searchText) {
		this.addItem(searchText);
		this.setParent(searchText, NavigationTree.SEARCH_HISTORY);
		// mark the saved search as a leaf (cannot have children)
		this.setChildrenAllowed(searchText, false);
		// make sure "Search" is expanded
		this.expandItem(NavigationTree.SEARCH_HISTORY);
		// select the saved search
		this.setValue(searchText);
	}

	public void addToConceptHistory(String uuid, String conceptLabel) {
		this.addItem(conceptLabel);
		this.setValue(conceptLabel);
		this.setParent(conceptLabel, NavigationTree.CONCEPT_HISTORY);
		// mark the saved search as a leaf (cannot have children)
		this.setChildrenAllowed(conceptLabel, false);
		// make sure "Search" is expanded
		this.expandItem(NavigationTree.CONCEPT_HISTORY);
		// select the saved search
	}
	
	

}
