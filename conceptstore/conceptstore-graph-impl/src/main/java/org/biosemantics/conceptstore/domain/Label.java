package org.biosemantics.conceptstore.domain;

import java.util.Collection;

public interface Label {

	public abstract long getId();

	public abstract String getText();

	public abstract String getLanguage();

	public abstract Collection<Concept> getRelatedConcepts();

}