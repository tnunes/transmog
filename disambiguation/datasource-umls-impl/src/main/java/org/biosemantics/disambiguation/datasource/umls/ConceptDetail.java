package org.biosemantics.disambiguation.datasource.umls;

import java.util.ArrayList;
import java.util.List;

public class ConceptDetail {
	private List<LabelDetail> labels = new ArrayList<LabelDetail>();
	private List<NotationDetail> notations = new ArrayList<NotationDetail>();

	public List<NotationDetail> getNotations() {
		return notations;
	}

	public List<LabelDetail> getLabels() {
		return labels;
	}

	public void addNotation(NotationDetail notationDetail) {
		this.notations.add(notationDetail);
	}

	public void addLabel(LabelDetail labelDetail) {
		labels.add(labelDetail);
	}

}
