package org.biosemantics.disambiguation.knowledgebase.service;

public interface LabelService {
	Label createPreferredLabel(String text, Language language);
	Label createAlternateLabel(String text, Language language);
}
