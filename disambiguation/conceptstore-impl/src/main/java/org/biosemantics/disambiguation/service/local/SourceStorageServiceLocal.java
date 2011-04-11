package org.biosemantics.disambiguation.service.local;

import org.biosemantics.conceptstore.common.domain.Source.SourceType;
import org.biosemantics.conceptstore.common.service.SourceStorageService;
import org.neo4j.graphdb.Node;

public interface SourceStorageServiceLocal extends SourceStorageService{

	Node getSourceNode(String value, SourceType sourceType);

}
