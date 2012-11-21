package org.biosemantics.conceptstore.domain.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.domain.HasLabel;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Objects;

public class HasLabelImpl implements HasLabel {

	public HasLabelImpl(Relationship relationship) {
		this.relationship = relationship;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.HasLabel#getType()
	 */
	@Override
	public String getType() {
		return relationship.getType().name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.HasLabel#getLabelType()
	 */
	@Override
	public LabelType getLabelType() {
		return LabelType.valueOf((String) relationship.getProperty("type"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.domain.impl.HasLabel#getSources()
	 */
	@Override
	public Collection<String> getSources() {
		return new HashSet<String>(Arrays.asList((String[]) relationship.getProperty("sources")));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof HasLabelImpl && relationship.equals(((HasLabelImpl) obj).relationship);
	}

	@Override
	public int hashCode() {
		return relationship.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("relationshipId", relationship.getId())
				.add("labelType", getLabelType()).add("sources", getSources()).toString();
	}

	private Relationship relationship;

}
