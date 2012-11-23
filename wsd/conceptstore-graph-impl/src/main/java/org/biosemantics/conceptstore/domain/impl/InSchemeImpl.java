package org.biosemantics.conceptstore.domain.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.domain.InScheme;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Objects;

public class InSchemeImpl implements InScheme {

	public InSchemeImpl(Relationship relationship) {
		this.relationship = relationship;
	}

	@Override
	public String getType() {
		return relationship.getType().name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.InScheme#getSources()
	 */
	@Override
	public Collection<String> getSources() {
		return new HashSet<String>(Arrays.asList((String[]) relationship.getProperty("sources")));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof InSchemeImpl && relationship.equals(((InSchemeImpl) obj).relationship);
	}

	@Override
	public int hashCode() {
		return relationship.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("relationshipId", relationship.getId())
				.add("type", relationship.getType()).add("sources", getSources()).toString();
	}

	private Relationship relationship;

}
