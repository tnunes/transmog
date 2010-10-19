package org.biosemantics.disambiguation.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.service.LabelStorageService;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.service.IndexService;
import org.neo4j.graphdb.Node;
import org.springframework.transaction.annotation.Transactional;

public class LabelStorageServiceImpl implements LabelStorageService {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node labelParentNode;

	private boolean checkExists;
	private IndexService indexService;

	public LabelStorageServiceImpl(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = checkNotNull(graphStorageTemplate);
		this.labelParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.LABELS);
		this.checkExists = true;
	}

	public void setCheckExists(boolean checkExists) {
		this.checkExists = checkExists;
	}

	public void setIndexService(IndexService indexService) {
		this.indexService = indexService;
	}

	@Override
	@Transactional
	public Label createLabel(Label label) {
		Label createdLabel = null;
		if (checkExists) {
			// check if node exits in data store
			createdLabel = findLabel(label);
		}
		if (createdLabel == null) {
			// create new node if none exists
			Node node = graphStorageTemplate.getGraphDatabaseService().createNode();
			graphStorageTemplate.createRelationship(labelParentNode, node, DefaultRelationshipType.LABEL);
			createdLabel = new LabelImpl(node).withLabelType(label.getLabelType()).withLanguage(label.getLanguage())
					.withText(label.getText());
			indexService.indexLabel(createdLabel);
		}
		return createdLabel;
	}

	private Label findLabel(Label label) {
		Label found = null;
		Iterable<Label> labels = indexService.getLabelsByText(label.getText());
		if (labels != null) {
			for (Label foundLabel : labels) {
				if (foundLabel.getLabelType() == label.getLabelType()
						&& foundLabel.getLanguage() == label.getLanguage()
						&& foundLabel.getText().equals(label.getText())) {
					found = foundLabel;
					break;
				}
			}
		}
		return found;
	}

	@Override
	@Transactional
	public Collection<Label> getLabelByText(String text) {
		return indexService.getLabelsByText(text);
	}

}
