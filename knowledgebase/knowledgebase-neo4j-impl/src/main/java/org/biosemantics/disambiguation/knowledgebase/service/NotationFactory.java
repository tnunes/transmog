package org.biosemantics.disambiguation.knowledgebase.service;

public interface NotationFactory {
	Notation createNotation(Domain domain, String text);
}
