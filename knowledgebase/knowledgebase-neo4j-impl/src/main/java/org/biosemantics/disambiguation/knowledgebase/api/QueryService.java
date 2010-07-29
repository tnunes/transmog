package org.biosemantics.disambiguation.knowledgebase.api;

public interface QueryService {
	Iterable<Label> getLabels(String text);
}
