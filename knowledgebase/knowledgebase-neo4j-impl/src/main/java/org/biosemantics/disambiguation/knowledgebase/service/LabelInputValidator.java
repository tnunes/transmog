package org.biosemantics.disambiguation.knowledgebase.service;

import java.util.Collection;


public interface LabelInputValidator {
	void validateLabels(Collection<Label> labels);
	void validateLabel(Label label);
	void validateText(String text);
	void validateLanguage(Language language);
}
