package org.biosemantics.conceptstore.repository;

import org.biosemantics.conceptstore.domain.Concept;
import org.springframework.data.neo4j.repository.TraversalRepository;


public interface ConceptTraversalRepository extends TraversalRepository<Concept> {
	
}
