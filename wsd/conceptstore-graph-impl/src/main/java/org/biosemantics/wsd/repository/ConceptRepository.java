package org.biosemantics.wsd.repository;

import org.biosemantics.wsd.domain.Concept;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ConceptRepository extends GraphRepository<Concept> {
	
	Concept getConceptById(String id);
	
	@Query("start concept=node({0}) match concept-[:RELATED]-otherConcept return otherConcept")
	Iterable<Concept> getRelatedConcepts(Concept concept);
	
}
