package org.biosemantics.disambiguation.conceptstore.web.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StorageCache{

	private Map<String, String> concepts = new HashMap<String, String>();

	public void putConcept(String preferredLabelText, String uuid) {
		concepts.put(preferredLabelText, uuid);
	}

	public Collection<String> getAllConceptLabels() {
		return concepts.keySet();
	}

	public String getUuid(String preferredLabelText) {
		return concepts.get(preferredLabelText);
	}
}
