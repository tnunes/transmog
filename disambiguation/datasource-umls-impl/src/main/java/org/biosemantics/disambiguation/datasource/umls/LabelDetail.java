package org.biosemantics.disambiguation.datasource.umls;

import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;

public class LabelDetail {
	private String text;
	private Language language;
	private LabelType labelType;

	public LabelDetail(String text, Language language, LabelType labelType) {
		super();
		this.text = text;
		this.language = language;
		this.labelType = labelType;
	}

	public LabelDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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
