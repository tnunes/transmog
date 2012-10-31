package org.biosemantics.disambiguation.conceptstore.web.widget;

import org.biosemantics.disambiguation.conceptstore.web.common.StorageUtility;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class AlgorithmOutput extends VerticalLayout {

	private static final Logger logger = LoggerFactory.getLogger(AlgorithmOutput.class);

	public AlgorithmOutput(Iterable<Path> paths) {
		this.setMargin(true);
		Table pathTable = new Table();
		// pathTable.setPageLength(10);
		pathTable.setWidth("100%");
		pathTable.addContainerProperty("Length", Integer.class, null);
		pathTable.addContainerProperty("Path", String.class, null);
		int numberOfPaths = 0;
		for (Path path : paths) {
			StringBuilder stringBuilder = new StringBuilder();
			// Iterator<PropertyContainer> propertyContainerIterator = path.iterator();
			Node previousNode = null;
			for (PropertyContainer propertyContainer : path) {
				if (propertyContainer instanceof Node) {
					Node node = (Node) propertyContainer;
					previousNode = node;
					String labeltext = StorageUtility.getPreferredLabel(new ConceptImpl(node), null).getText();
					stringBuilder.append(labeltext);
				} else if (propertyContainer instanceof Relationship) {
					Relationship rlsp = (Relationship) propertyContainer;
					if (rlsp.getStartNode().equals(previousNode)) {
						stringBuilder.append("--").append(rlsp.getType().name()).append("->");
					} else {
						stringBuilder.append("<-").append(rlsp.getType().name()).append("--");
					}
				}
			}
			pathTable.addItem(new Object[] { path.length(), stringBuilder.toString() }, ++numberOfPaths);
		}
		pathTable.setCaption(numberOfPaths +" paths available. Summary:");
		logger.debug("{} paths found.", numberOfPaths);
		this.addComponent(pathTable);
	}
}
