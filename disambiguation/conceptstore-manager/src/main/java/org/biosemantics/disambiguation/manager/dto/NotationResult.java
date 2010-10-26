package org.biosemantics.disambiguation.manager.dto;

import org.biosemantics.conceptstore.common.domain.Language;

public class NotationResult {

	private String domainLabel;
	private Language domainLabelLanguage;
	private String code;

	public NotationResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NotationResult(String domainLabel, Language domainLabelLanguage, String code) {
		super();
		this.domainLabel = domainLabel;
		this.domainLabelLanguage = domainLabelLanguage;
		this.code = code;
	}

	public String getDomainLabel() {
		return domainLabel;
	}

	public void setDomainLabel(String domainLabel) {
		this.domainLabel = domainLabel;
	}

	public Language getDomainLabelLanguage() {
		return domainLabelLanguage;
	}

	public void setDomainLabelLanguage(Language domainLabelLanguage) {
		this.domainLabelLanguage = domainLabelLanguage;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
