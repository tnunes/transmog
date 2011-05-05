package org.biosemantics.datasource.umls.cache;

public interface UmlsCacheService {
	void add(KeyValue keyValue);

	String getValue(String key);
}
