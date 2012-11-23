package org.biosemantics.conceptstore.repository;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.HasLabel;
import org.biosemantics.conceptstore.domain.HasNotation;
import org.biosemantics.conceptstore.domain.HasRlsp;
import org.biosemantics.conceptstore.domain.InScheme;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.domain.impl.LabelType;

public interface ConceptRepository {

	public abstract Concept create(ConceptType conceptType);

	public abstract HasLabel hasLabel(long conceptId, long labelId, LabelType labelType, String... sources);

	public abstract HasLabel hasLabelIfNoneExists(long conceptId, long labelId, LabelType labelType, String... sources);

	public abstract HasNotation hasNotation(long conceptId, long notationId, String... sources);

	public abstract HasNotation hasNotationIfNoneExists(long conceptId, long notationId, String... sources);

	public abstract HasRlsp hasRlsp(long fromConceptId, long toConceptId, String relationshipType, String... sources);

	public abstract HasRlsp hasRlspIfNoneExists(long fromConceptId, long toConceptId, String relationshipType,
			String... sources);

	public abstract HasRlsp hasRlspIfNoBidirectionalRlspExists(long fromConceptId, long toConceptId,
			String relationshipType, String... sources);

	public abstract Concept getById(long id);

	public abstract Collection<Concept> getByType(ConceptType conceptType);

	public abstract Collection<HasRlsp> getAllHasRlspsForConcept(long id);

	public abstract InScheme addInScheme(long conceptId, long conceptSchemeId, String... sources);

	public abstract Collection<Long> getAllChildPredicates(Long predicateConceptId);

}