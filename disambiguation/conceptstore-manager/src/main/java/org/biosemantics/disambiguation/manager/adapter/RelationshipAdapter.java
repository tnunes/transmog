package org.biosemantics.disambiguation.manager.adapter;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Relationship;
import org.biosemantics.disambiguation.manager.dto.RelationshipResult;

public interface RelationshipAdapter {

	RelationshipResult adapt(Concept concept, Relationship relationship);

}
