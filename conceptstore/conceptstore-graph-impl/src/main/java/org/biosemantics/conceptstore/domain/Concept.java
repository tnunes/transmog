package org.biosemantics.conceptstore.domain;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.impl.ConceptType;

/**
 * A concept is a unit of thought and is in essence not lingual. By virtue
 * of being a discrete unit of thought, concepts are intrinsically
 * non-ambiguous.
 * 
 * @author bhsingh
 * @since 1.6
 */
public interface Concept {
	/**
	 * Retrieves all labels associated with this concept
	 * 
	 * @return a {@link Notation} of labels associated with this concept
	 */
	public abstract Collection<Label> getLabels();

	/**
	 * Retrieves all notations associated with this concept
	 * 
	 * @return a {@code Collection} of {@link Notation} associated with this
	 *         concept
	 */
	public abstract Collection<Notation> getNotations();

	/**
	 * retrieves the type for a concept. All possible types are defined in the
	 * {@link ConceptType} enum.
	 * 
	 * @return
	 */
	public abstract ConceptType getType();

	/**
	 * Retrieves the Id associated with this concept. Id is the unique key
	 * associated with this concept, however it is not guaranteed to remain the
	 * same across system restarsts, hence long term caching based on Id is not
	 * recommended.
	 * 
	 * @return the id associated with this concept
	 */
	public abstract long getId();

	/**
	 * retrieves the concept schemes this concept may be a part of. Concept
	 * schemes are defined as the semantic types for this concept.
	 * 
	 * @return {@link Collection} of semantic types that this concept may be a
	 *         part of. Returns an empty collection if the concept is not a part
	 *         of any copncept scheme.
	 */
	public abstract Collection<Concept> getInSchemes();

}