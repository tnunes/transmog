package org.biosemantics.disambiguation.service.local;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.service.LabelStorageService;
import org.neo4j.graphdb.Node;

public interface LabelStorageServiceLocal extends LabelStorageService {

	Node getLabelNode(long id);
	
	Node createLabelNode(Label label);
	
	Node getLabelNode(String text, Language language);

}
