package org.biosemantics.wsd.repository;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Notation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface NotationRepository extends GraphRepository<Notation>{
	
	
	@Query("start notation=node({0}) match notation<-[:HAS_NOTATION]-concept return concept")
	Concept getRelatedConcept(Notation notation);

}