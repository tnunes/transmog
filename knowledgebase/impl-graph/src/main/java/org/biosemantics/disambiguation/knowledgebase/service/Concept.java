package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;

public interface Concept {
	String getId();
	Collection<Label> getLabels();
	Collection<Notation> getNotations();
}