package org.biosemantics.conceptstore.repository;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.Notation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface NotationRepository extends GraphRepository<Notation> {

	@Query("start notation=node:Notation(code={1}) where notation.source={0} return notation")
	Notation getNotation(String source, String code);

	@Query("start notation=node:Notation(code={1}) match notation<-[:HAS_NOTATION]-concept where notation.source={0} return concept")
	Concept getRelatedConcept(String source, String code);

}
