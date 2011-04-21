package org.biosemantics.disambiguation.service.local.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.time.StopWatch;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.utils.service.UuidGeneratorService;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.service.local.LabelStorageServiceLocal;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class LabelStorageServiceLocalImpl implements LabelStorageServiceLocal {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node labelParentNode;
	private final Index<Node> index;
	private UuidGeneratorService uuidGeneratorService;
	private ValidationUtility validationUtility;
	public static final String LABEL_INDEX = "label";
	public static final String UUID_INDEX_KEY = "label_uuid";
	public static final String TEXT_INDEX_KEY = "label_text";
	private static final Logger logger = LoggerFactory.getLogger(LabelStorageServiceLocalImpl.class);

	public LabelStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
		this.labelParentNode = this.graphStorageTemplate.getParentNode(DefaultRelationshipType.LABELS);
		this.index = graphStorageTemplate.getIndexManager().forNodes(LABEL_INDEX);
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Required
	public void setUuidGeneratorService(UuidGeneratorService uuidGeneratorService) {
		this.uuidGeneratorService = uuidGeneratorService;
	}

	@Override
	@Transactional
	public String createLabel(Label label) {
		validationUtility.validateLabel(label);
		Node node = createLabelNode(label);
		return (String) node.getProperty(LabelImpl.UUID_PROPERTY);
	}

	public Node createLabelNode(Label label) {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		String uuid = uuidGeneratorService.generateRandomUuid();
		Node labelNode = graphStorageTemplate.getGraphDatabaseService().createNode();
		graphStorageTemplate.createRelationship(labelParentNode, labelNode, DefaultRelationshipType.LABEL);
		labelNode.setProperty(LabelImpl.UUID_PROPERTY, uuid);
		labelNode.setProperty(LabelImpl.LANGUAGE_PROPERTY, label.getLanguage().getLabel());
		labelNode.setProperty(LabelImpl.TEXT_PROPERTY, label.getText());
		index.add(labelNode, UUID_INDEX_KEY, uuid);
		index.add(labelNode, TEXT_INDEX_KEY, label.getText());
		stopwatch.stop();
		logger.debug("createLabelNode() time: {}", stopwatch.getTime());
		return labelNode;
	}

	@Override
	public Label getLabel(String uuid) {
		validationUtility.validateString(uuid, "uuid");
		return new LabelImpl(getLabelNode(uuid));
	}

	@Override
	public Collection<Label> getLabelsByText(String text) {
		validationUtility.validateString(text, "text");
		IndexHits<Node> nodes = index.get(TEXT_INDEX_KEY, text);
		Collection<Label> labels = new HashSet<Label>();
		for (Node node : nodes) {
			if (((String) node.getProperty(LabelImpl.TEXT_PROPERTY)).equals(text)) {
				labels.add(new LabelImpl(node));
			}
		}
		return labels;

	}

	@Override
	public Node getLabelNode(String uuid) {
		return index.get(UUID_INDEX_KEY, uuid).getSingle();
	}

	@Override
	public Node getLabelNode(String text, Language language) {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		Node found = null;
		IndexHits<Node> nodes = index.get(TEXT_INDEX_KEY, text);
		for (Node node : nodes) {
			if (((String) node.getProperty(LabelImpl.TEXT_PROPERTY)).equals(text)
					&& language.getLabel().equals((String) node.getProperty(LabelImpl.LANGUAGE_PROPERTY))) {
				found = node;
				break;
			}
		}
		stopwatch.stop();
		logger.debug("getLabelNode() time: {}", stopwatch.getTime());
		return found;
	}

}
