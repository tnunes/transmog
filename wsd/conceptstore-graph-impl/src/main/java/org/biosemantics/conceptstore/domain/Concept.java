package org.biosemantics.conceptstore.domain;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.impl.ConceptType;

public interface Concept {

	public abstract Collection<Label> getLabels();

	public abstract Collection<Notation> getNotations();

	public abstract ConceptType getType();

	public abstract long getId();

	public abstract Collection<Concept> getInSchemes();

}