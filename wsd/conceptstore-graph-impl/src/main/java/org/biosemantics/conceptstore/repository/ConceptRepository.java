package org.biosemantics.conceptstore.repository;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.Label;
import org.biosemantics.conceptstore.domain.Notation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ConceptRepository extends GraphRepository<Concept> {

	@Query("start concept=node({0}) match concept--otherConcept where otherConcept.type=\"CONCEPT\" return otherConcept")
	Iterable<Concept> getRelatedConcepts(Concept concept);

	@Query("start concept=node({0}) match concept-[:CHILD]-otherConcept return otherConcept")
	Iterable<Concept> getHierarchicalConcepts(Concept concept);

	@Query("start concept=node({0}) match concept<-[:CHILD]-otherConcept return otherConcept")
	Iterable<Concept> getChildConcepts(Concept concept);

	@Query("start concept=node({0}) match concept-[:CHILD]->otherConcept return otherConcept")
	Iterable<Concept> getParentConcepts(Concept concept);

	@Query("start concept=node({0}) match concept-[r:HAS_LABEL]->label where r.type = \"PREFERRED\" and label.language = {1} return label")
	Label getPreferredLabel(Concept concept, String language);

	@Query("start concept=node({0}) match concept-[r:HAS_LABEL]->label where r.type = \"PREFERRED\" return label")
	Iterable<Label> getPreferredLabels(Concept concept);

	@Query("start concept=node({0}) match concept-[:IN_SCHEME]-otherConcept return otherConcept")
	Iterable<Concept> getConceptSchemes(Concept concept);

	/**
	 * @deprecated use the getNotations method provided in the concept object
	 * @param concept
	 * @return
	 */
	@Deprecated
	@Query("start concept=node({0}) match concept-[r:HAS_NOTATION]->notation  return notation")
	Iterable<Notation> getNotations(Concept concept);

}
