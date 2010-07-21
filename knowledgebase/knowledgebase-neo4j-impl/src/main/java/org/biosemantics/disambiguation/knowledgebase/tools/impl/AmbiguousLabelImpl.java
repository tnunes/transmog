package org.biosemantics.disambiguation.knowledgebase.tools.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.disambiguation.knowledgebase.tools.AmbiguousLabel;

public class AmbiguousLabelImpl implements AmbiguousLabel {
	private String labelId;
	private List<String> associatedConceptIds = new ArrayList<String>();
	
	public AmbiguousLabelImpl(String labelId) {
		this.labelId = labelId;
	}

	public String getLabelId() {
		return labelId;
	}
	
	public void setAssociatedConceptIds(String... conceptIds){
		for (String conceptId : conceptIds) {
			this.associatedConceptIds.add(conceptId);
		}
	}
	
	@Override
	public Collection<String> getAssociatedConceptIds() {
		return associatedConceptIds;
	}
	
	
	
}
