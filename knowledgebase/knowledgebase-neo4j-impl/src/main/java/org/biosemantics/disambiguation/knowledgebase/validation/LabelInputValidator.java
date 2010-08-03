package org.biosemantics.disambiguation.knowledgebase.validation;

import java.util.Collection;

import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.Language;


public interface LabelInputValidator {
	void validateLabels(Collection<Label> labels);
	void validateLabel(Label label);
	void validateText(String text);
	void validateLanguage(Language language);
}
