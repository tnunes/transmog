package org.biosemantics.disambiguation.manager.dto;

import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;

public class LabelResult {
	private String labelText;
	private Language language;
	private LabelType labelType;

	public LabelResult(Language language, LabelType labelType, String labelText) {
		super();
		this.labelText = labelText;
		this.language = language;
		this.labelType = labelType;
	}

	public LabelResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public LabelType getLabelType() {
		return labelType;
	}

	public void setLabelType(LabelType labelType) {
		this.labelType = labelType;
	}

}
