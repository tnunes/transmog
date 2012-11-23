package org.biosemantics.conceptstore.domain;

import java.util.Collection;

public interface Notation {

	public abstract Collection<Concept> getRelatedConcepts();

	public abstract String getSource();

	public abstract String getCode();

	public abstract long getId();

}