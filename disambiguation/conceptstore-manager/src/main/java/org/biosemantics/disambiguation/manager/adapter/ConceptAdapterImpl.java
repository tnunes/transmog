package org.biosemantics.disambiguation.manager.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.disambiguation.manager.common.CommonUtility;
import org.biosemantics.disambiguation.manager.dto.ConceptResult;
import org.biosemantics.disambiguation.manager.dto.LabelResult;
import org.biosemantics.disambiguation.manager.dto.NotationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class ConceptAdapterImpl implements ConceptAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ConceptAdapterImpl.class);
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
		Collection<Notation> conceptNotations = concept.getNotations();
		if (! CollectionUtils.isEmpty(conceptNotations)) {
			for (Notation conceptNotation : conceptNotations) {
				long start = System.currentTimeMillis();
				notations.add(adapt(conceptNotation));
				logger.info(" adapt for notations in {}:ms", (System.currentTimeMillis()-start));
			}
		}
		return new ConceptResult(concept.getUuid(), CommonUtility.getPreferredLabel(
				concept.getLabelsByType(LabelType.PREFERRED)).getText(), labels, notations);
	}

	private LabelResult adapt(Label label, LabelType labelType) {
		return new LabelResult(label.getLanguage(), labelType, label.getText());
	}

	private NotationResult adapt(Notation notation) {
		long start = System.currentTimeMillis();
		Concept domain = notation.getDomain();
		logger.info(" getDomain in {}:ms", (System.currentTimeMillis()-start));
		start = System.currentTimeMillis();
		Label preferredLabel = CommonUtility.getPreferredLabel(domain.getLabelsByType(LabelType.PREFERRED));
		logger.info(" get Label in {}:ms", (System.currentTimeMillis()-start));
		return new NotationResult(preferredLabel.getText(), preferredLabel.getLanguage(), notation.getCode());
	}

}
