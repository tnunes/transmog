package org.biosemantics.disambiguation.bulkimport.service;

public interface NodeCacheService {

	void addLabel(String text, String language, long nodeId);

	void addNotation(String domainUuid, String code, long nodeId);

	long getLabelNodeId(final String text, final String language);

	long getNotationNodeId(final String domainUuid, final String code);

	void addConcept(String uuid, long nodeId);

	long getConceptNodeId(String uuid);

	void addConceptRelationship(String key, long relationshipId);

	long getConceptRelationshipId(String key);

}
