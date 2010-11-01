package org.biosemantics.disambiguation.manager.adapter;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.disambiguation.manager.dto.QueryResult;

public interface QueryResultAdapter {

	QueryResult adapt(Concept concept);

}
