package org.biosemantics.disambiguation.knowledgebase.api;

public interface LabelFactory {
	Label createPreferredLabel(String text, Language language);
	Label createAlternateLabel(String text, Language language);
}
