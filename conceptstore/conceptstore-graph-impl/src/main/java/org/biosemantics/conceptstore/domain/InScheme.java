package org.biosemantics.conceptstore.domain;

import java.util.Collection;

public interface InScheme {

	public abstract Collection<String> getSources();

	public abstract String getType();

}