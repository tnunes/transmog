package org.biosemantics.disambiguation.knowledgebase.tools;

import java.util.Collection;

public interface AmbiguousLabel {
	String getLabelId();
	Collection<String> getAssociatedConceptIds();
}
