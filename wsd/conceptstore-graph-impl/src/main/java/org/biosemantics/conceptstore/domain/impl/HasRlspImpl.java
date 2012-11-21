package org.biosemantics.conceptstore.domain.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.domain.HasRlsp;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Objects;

public class HasRlspImpl implements HasRlsp {

	public HasRlspImpl(Relationship relationship) {
		this.relationship = relationship;
	}

	/* (non-Javadoc)
	 * @see org.biosemantics.conceptstore.domain.impl.HasRlsp#getSources()
	 */
	@Override
	public Collection<String> getSources() {
		return new HashSet<String>(Arrays.asList((String[]) relationship.getProperty("sources")));
	}

	/* (non-Javadoc)
	 * @see org.biosemantics.conceptstore.domain.impl.HasRlsp#getType()
	 */
	@Override
	public String getType() {
		return relationship.getType().name();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof HasRlspImpl && relationship.equals(((HasRlspImpl) obj).relationship);
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
