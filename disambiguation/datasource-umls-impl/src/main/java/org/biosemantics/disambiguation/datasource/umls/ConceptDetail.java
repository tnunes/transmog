package org.biosemantics.disambiguation.datasource.umls;

import java.util.Collection;
import java.util.HashSet;

public class ConceptDetail {
	private Collection<LabelDetail> labels = new HashSet<LabelDetail>();
	private Collection<NotationDetail> notations = new HashSet<NotationDetail>();

	public Collection<LabelDetail> getLabels() {
		return labels;
	}

	public Collection<NotationDetail> getNotations() {
		return notations;
	}

	public void addNotation(NotationDetail notationDetail) {
		this.notations.add(notationDetail);
	}

	public void addLabel(LabelDetail labelDetail) {
		labels.add(labelDetail);
	}

}
