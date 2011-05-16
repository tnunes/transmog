package org.biosemantics.disambiguation.conceptstore.web.widget;

import org.biosemantics.conceptstore.common.domain.ConceptLabel;

import com.vaadin.ui.Label;

public class ConceptHtmlLabel extends Label {
	private String uuid;
	
	public ConceptHtmlLabel(String uuid, ConceptLabel conceptLabel) {
		this.uuid = uuid;
		this.setContentMode(Label.CONTENT_XHTML);
		StringBuilder html = new StringBuilder(conceptLabel.getText());
		this.setValue(html.toString());
	}

	public String getUuid() {
		return uuid;
	}

}