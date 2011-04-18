package org.biosemantics.disambiguation.script.impl;

import java.util.List;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.disambiguation.script.OutputObject;

public class AmbiguousLabelOutputObject implements OutputObject {

	private Label label;
	private List<String> concepts;

	public AmbiguousLabelOutputObject(Label label, List<String> concepts) {
		super();
		this.label = label;
		this.concepts = concepts;
	}

	@Override
	public String[] toStringArray() {
		String[] output = new String[5];
		output[0] = label.getUuid();
		output[1] = label.getLanguage().getLabel();
		output[2] = label.getText();
		output[3] = String.valueOf(concepts.size());
		output[4] = concepts.toString();
		return output;
	}
}
