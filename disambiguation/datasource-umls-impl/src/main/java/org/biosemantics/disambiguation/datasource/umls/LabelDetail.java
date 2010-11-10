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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((labelType == null) ? 0 : labelType.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabelDetail other = (LabelDetail) obj;
		if (labelType != other.labelType)
			return false;
		if (language != other.language)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

}
