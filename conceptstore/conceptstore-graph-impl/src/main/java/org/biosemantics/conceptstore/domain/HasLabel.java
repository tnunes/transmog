package org.biosemantics.conceptstore.domain;

import org.biosemantics.conceptstore.domain.impl.LabelType;

public interface HasLabel extends Sourceable {

	public abstract String getType();

	public abstract LabelType getLabelType();

}