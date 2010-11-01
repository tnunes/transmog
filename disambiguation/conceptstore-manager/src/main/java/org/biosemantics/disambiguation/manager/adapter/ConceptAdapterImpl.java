package org.biosemantics.disambiguation.manager.adapter;

import java.util.ArrayList;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.disambiguation.manager.common.CommonUtility;
import org.biosemantics.disambiguation.manager.dto.ConceptResult;
import org.biosemantics.disambiguation.manager.dto.LabelResult;
import org.biosemantics.disambiguation.manager.dto.NotationResult;

public class ConceptAdapterImpl implements ConceptAdapter {

	@Override
	public ConceptResult adapt(Concept concept) {
		List<LabelResult> labels = new ArrayList<LabelResult>();
		List<NotationResult> notations = new ArrayList<NotationResult>();
		for (Label label : concept.getLabelsByType(LabelType.PREFERRED)) {
			labels.add(adapt(label, LabelType.PREFERRED));
		}
		for (Label label : concept.getLabelsByType(LabelType.ALTERNATE)) {
			labels.add(adapt(label, LabelType.ALTERNATE));
		}
		if (concept.getNotations() != null && !concept.getNotations().isEmpty()) {
			for (Notation notation : concept.getNotations()) {
				notations.add(adapt(notation));
			}
		}

		return new ConceptResult(concept.getUuid(), CommonUtility.getPreferredLabel(
				concept.getLabelsByType(LabelType.PREFERRED)).getText(), labels, notations);
	}

	private LabelResult adapt(Label label, LabelType labelType) {
		return new LabelResult(label.getLanguage(), labelType, label.getText());
	}

	private NotationResult adapt(Notation notation) {
		Concept domain = notation.getDomain();
		Label preferredLabel = CommonUtility.getPreferredLabel(domain.getLabelsByType(LabelType.PREFERRED));
		return new NotationResult(preferredLabel.getText(), preferredLabel.getLanguage(), notation.getCode());
	}

}
