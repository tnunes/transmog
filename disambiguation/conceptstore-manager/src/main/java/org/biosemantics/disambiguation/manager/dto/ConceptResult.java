package org.biosemantics.disambiguation.manager.dto;

import java.util.Collection;

public class ConceptResult {
	private String uuid;
	private String preferredLabel;
	private Collection<LabelResult> labels;
	private Collection<NotationResult> notations;

	public Collection<LabelResult> getLabels() {
		return labels;
	}

	public void setLabels(Collection<LabelResult> labels) {
		this.labels = labels;
	}

	public Collection<NotationResult> getNotations() {
		return notations;
	}

	public void setNotations(Collection<NotationResult> notations) {
		this.notations = notations;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPreferredLabel() {
		return preferredLabel;
	}

	public void setPreferredLabel(String preferredLabel) {
		this.preferredLabel = preferredLabel;
	}

	public ConceptResult(String uuid, String preferredLabel, Collection<LabelResult> labels,
			Collection<NotationResult> notations) {
		super();
		this.uuid = uuid;
		this.preferredLabel = preferredLabel;
		this.labels = labels;
		this.notations = notations;
	}

	public ConceptResult() {
		super();
		// TODO Auto-generated constructor stub
	}

}
