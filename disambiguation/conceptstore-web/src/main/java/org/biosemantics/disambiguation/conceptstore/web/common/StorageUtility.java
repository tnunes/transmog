package org.biosemantics.disambiguation.conceptstore.web.common;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;

public abstract class StorageUtility {

	public static final Language DEFAULT = LanguageImpl.EN;

	public static final ConceptLabel getPreferredLabel(Concept concept, Language language) {
		if (language == null) {
			language = DEFAULT;
		}
		ConceptLabel foundLabel = null;
		for (ConceptLabel conceptLabel : concept.getLabels()) {
			foundLabel = conceptLabel;
			if (conceptLabel.getLabelType() == LabelType.PREFERRED
					&& conceptLabel.getLanguage().getLabel().equals(language.getLabel())) {
				foundLabel = conceptLabel;
				return foundLabel;
			}
		}
		return foundLabel;
	}

}
