package org.biosemantics.datasource.umls.cache;

public interface UmlsCacheService {
	void add(KeyValue keyValue);

	String getValue(String key);

	void addDomainNode(KeyValue keyValue);

	String getDomainNode(String key);

	void addRelationship(String key);

	boolean getRelationship(String key);

}
