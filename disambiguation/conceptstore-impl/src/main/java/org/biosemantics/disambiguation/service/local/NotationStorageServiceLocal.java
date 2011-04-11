package org.biosemantics.disambiguation.service.local;

import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.common.service.NotationStorageService;
import org.neo4j.graphdb.Node;

public interface NotationStorageServiceLocal extends NotationStorageService {

	Node getNotationNode(String uuid);

	Node createNotationNode(Notation notation);

	Node getNotationNode(String code, String domainUuid);
}
