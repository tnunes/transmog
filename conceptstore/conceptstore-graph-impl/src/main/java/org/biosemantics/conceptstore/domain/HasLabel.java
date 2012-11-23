package org.biosemantics.conceptstore.domain;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.impl.LabelType;

public interface HasLabel {

	public abstract String getType();

	public abstract LabelType getLabelType();

	public abstract Collection<String> getSources();

}