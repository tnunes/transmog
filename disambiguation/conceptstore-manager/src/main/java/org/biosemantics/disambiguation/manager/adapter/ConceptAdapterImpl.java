package org.biosemantics.disambiguation.manager.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.disambiguation.manager.common.CommonConstants;
import org.biosemantics.disambiguation.manager.dto.ConceptResult;
import org.biosemantics.disambiguation.manager.dto.LabelResult;
import org.biosemantics.disambiguation.manager.dto.NotationResult;
import org.springframework.context.i18n.LocaleContextHolder;

public class ConceptAdapterImpl implements ConceptAdapter {

	@Override
	public ConceptResult adapt(Concept concept) {
		List<LabelResult> labels = new ArrayList<LabelResult>();
		List<NotationResult> notations = new ArrayList<NotationResult>();
		for (Label label : concept.getLabels()) {
			labels.add(adapt(label));
		}
		if (concept.getNotations() != null && !concept.getNotations().isEmpty()) {
			for (Notation notation : concept.getNotations()) {
				notations.add(adapt(notation));
			}
		}

		return new ConceptResult(concept.getUuid(), getPreferredLabel(concept.getLabels()).getText(), labels, notations);
	}

	private LabelResult adapt(Label label) {
		return new LabelResult(label.getLanguage(), label.getLabelType(), label.getText());
	}

	private NotationResult adapt(Notation notation) {
		Concept domain = notation.getDomain();
		Label preferredLabel = getPreferredLabel(domain.getLabels());
		return new NotationResult(preferredLabel.getText(), preferredLabel.getLanguage(), notation.getCode());
	}

	private Label getPreferredLabel(Collection<Label> labels) {
		Language localeLanguage = Language.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
		Label preferredLabel = null;
		for (Label label : labels) {
			if (label.getLabelType() == LabelType.PREFERRED) {
				if (label.getLanguage() == localeLanguage) {
					preferredLabel = label;
					break;
				} else if (label.getLanguage() == CommonConstants.defaultLanguage) {
					preferredLabel = label;
				} else {
					preferredLabel = label;
				}
			}
		}
		return preferredLabel;
	}
}
