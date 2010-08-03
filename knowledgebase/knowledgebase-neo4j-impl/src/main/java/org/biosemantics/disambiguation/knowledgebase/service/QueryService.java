package org.biosemantics.disambiguation.knowledgebase.service;

public interface QueryService {
	Iterable<Label> getLabels(String text);
}
