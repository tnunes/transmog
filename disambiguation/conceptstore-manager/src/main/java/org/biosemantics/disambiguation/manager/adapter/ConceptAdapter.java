package org.biosemantics.disambiguation.manager.adapter;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.disambiguation.manager.dto.ConceptResult;

public interface ConceptAdapter {
	ConceptResult adapt(Concept concept);
}
