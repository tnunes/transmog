package org.biosemantics.disambiguation.conceptstore.web.common;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;

public abstract class StorageUtility {

	public static final ConceptLabel getPreferredLabel(Concept concept, Language language) {
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
