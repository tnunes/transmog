package org.biosemantics.conceptstore.domain;

import java.util.Collection;

public interface HasRlsp extends Sourceable {

	public abstract String getType();

	public abstract Concept getStartConcept();

	public abstract Concept getOtherConcept(long id);

	public abstract Concept getEndConcept();

	public abstract Collection<Label> getLabels();

}