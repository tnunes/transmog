package org.biosemantics.disambiguation.knowledgebase.api;

public interface NotationFactory {
	Notation createNotation(Domain domain, String code);
}
