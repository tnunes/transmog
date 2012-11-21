package org.biosemantics.conceptstore.domain;

import java.util.*;

import org.biosemantics.conceptstore.domain.impl.*;

public interface Concept {

	public abstract Collection<Label> getLabels();

	public abstract Collection<Notation> getNotations();

	public abstract ConceptType getType();

	public abstract long getId();

}