package org.biosemantics.conceptstore.repository;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.Notation;

public interface NotationRepository {

	/**
	 * Force creates a new Notation irrespective of whether one with the same
	 * code and source exists or not.
	 * 
	 * @param source
	 *            source for the notation
	 * @param code
	 *            for the notation
	 * @return Notation object created
	 */
	public abstract Notation create(String source, String code);

	/**
	 * Retrieves a notation against this id. A {@link IllegalArgumentException}
	 * is thrown if no notation is found with this id. Id is retrieved from
	 * <code>Notation.getId()</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if no notation against this id is found
	 * @param id
	 *            against which notation is to be retrieved
	 * @return Notation for this id
	 */
	public abstract Notation getById(long id);

	/**
	 * Retrive all <code>Notations</code> that are related to this notation
	 * code. A code may have multiple notations associated with it, thus this
	 * method returns a collection of Notations
	 * 
	 * @param code
	 *            code against which notations are to be retrieved
	 * @return collection of notations against this code.
	 */
	public abstract Collection<Notation> getByCode(String code);

	/**
	 * Gets a Notation if one with this source and code exists. If no notation
	 * with this source and code exists a new Notation is created and returned.
	 * This API ensures no duplicate Notation objects exist in the store
	 * 
	 * @param source
	 *            for the notation
	 * @param code
	 *            for the notation
	 * @return Notation that exists in the store against the source and code or
	 *         a new Notation with this source and code.
	 */
	public abstract Notation getOrCreate(String source, String code);

}