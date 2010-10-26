package org.biosemantics.disambiguation.manager.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class UserPreferenceImpl implements UserPreference {

	private LinkedList<String> queryTexts = new LinkedList<String>();

	@Override
	public Collection<String> getPreviousSearchTexts() {
		// defensive copying, if we return the collection queryTexts it could be modified
		Collection<String> texts = new ArrayList<String>();
		Iterator<String> iterator = queryTexts.descendingIterator();
		while (iterator.hasNext()) {
			texts.add(iterator.next());
		}
		return texts;
	}

	@Override
	public void addSearchText(String searchText) {
		queryTexts.add(searchText);
	}

}
