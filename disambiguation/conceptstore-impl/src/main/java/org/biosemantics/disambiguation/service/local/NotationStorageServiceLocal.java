package org.biosemantics.disambiguation.service.local;

import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.NotationStorageService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;

public interface NotationStorageServiceLocal extends NotationStorageService {

	Node getNotationNode(long id);

	Node createNotationNode(Notation notation);

	Node getNotationNode(String code, String domainUuid);

	IndexHits<Node> getNotationNodes(String code);
}
