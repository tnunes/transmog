package org.biosemantics.disambiguation.service.local;

import org.biosemantics.conceptstore.common.service.ConceptStorageService;
import org.neo4j.graphdb.Node;

public interface ConceptStorageServiceLocal extends ConceptStorageService{
	
	Node getConceptNode(String uuid);

}
