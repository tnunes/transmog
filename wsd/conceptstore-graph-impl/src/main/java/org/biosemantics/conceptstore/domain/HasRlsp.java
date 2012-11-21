package org.biosemantics.conceptstore.domain;

import java.util.Collection;

public interface HasRlsp {

	public abstract Collection<String> getSources();

	public abstract String getType();

}