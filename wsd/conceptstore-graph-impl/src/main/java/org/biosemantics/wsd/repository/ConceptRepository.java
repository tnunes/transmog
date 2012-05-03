package org.biosemantics.wsd.repository;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Label;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ConceptRepository extends GraphRepository<Concept> {
	
	Concept getConceptById(String id);
	
	@Query("start concept=node({0}) match concept-[:RELATED]-otherConcept return otherConcept")
	Iterable<Concept> getRelatedConcepts(Concept concept);
	
	@Query("start concept=node({0}) match concept-[r:HAS_LABEL]-label where r.type = \"PREFERRED\" and label.language = {1} return label ")
	Label getPreferredLabel(Concept concept, String language);
	
	@Query("start concept=node({0}) match concept-[:IN_SCHEME]-otherConcept return otherConcept")
	Iterable<Concept> getConceptSchemes(Concept concept);
	
}
