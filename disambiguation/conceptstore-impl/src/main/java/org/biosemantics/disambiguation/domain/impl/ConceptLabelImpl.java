package org.biosemantics.disambiguation.domain.impl;

import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;

public class ConceptLabelImpl implements ConceptLabel {

	private static final long serialVersionUID = 4856578025734575634L;
	private Label label;
	private LabelType labelType;

	public ConceptLabelImpl(Label label, LabelType labelType) {
		this.label = label;
		this.labelType = labelType;
	}

	@Override
	public long getId() {
		return label.getId();
	}

	@Override
	public String getText() {
		return label.getText();
	}

	@Override
	public Language getLanguage() {
		return label.getLanguage();
	}

	@Override
	public LabelType getLabelType() {
		return labelType;
	}

	@Override
	public String[] getTags() {
		return label.getTags();
	}
}
