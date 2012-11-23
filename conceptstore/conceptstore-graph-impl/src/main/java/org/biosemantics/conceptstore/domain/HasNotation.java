package org.biosemantics.conceptstore.domain;

import java.util.Collection;

public interface HasNotation {

	public abstract Collection<String> getSources();

	public abstract String getType();

}