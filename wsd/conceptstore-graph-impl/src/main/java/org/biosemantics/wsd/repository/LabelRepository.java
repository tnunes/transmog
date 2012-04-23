package org.biosemantics.wsd.repository;

import java.util.List;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Label;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface LabelRepository extends GraphRepository<Label>{
	@Query("start label=node({0}) match label-[:HAS_LABEL]-otherConcept return otherConcept")
	Iterable<Concept> getRelatedConcepts(Label label);

}
