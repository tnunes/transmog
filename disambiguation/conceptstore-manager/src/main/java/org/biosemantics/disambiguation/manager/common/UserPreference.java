package org.biosemantics.disambiguation.manager.common;

import java.util.Collection;

public interface UserPreference {
	Collection<String> getPreviousSearchTexts();

	void addSearchText(String searchText);

}
