package org.biosemantics.disambiguation.service.local;

import org.biosemantics.conceptstore.common.service.ConceptRelationshipStorageService;
import org.neo4j.graphdb.Relationship;

public interface ConceptRelationshipStorageServiceLocal extends ConceptRelationshipStorageService{
	
	Relationship getRelationship(String uuid);

}
