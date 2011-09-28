package org.biosemantics.disambiguation.service.local.impl;

import static org.biosemantics.disambiguation.common.IndexConstant.LABEL_INDEX;
import static org.biosemantics.disambiguation.common.IndexConstant.LABEL_TEXT_KEY;

import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.utils.validation.ValidationUtility;
import org.biosemantics.disambiguation.common.PropertyConstant;
import org.biosemantics.disambiguation.common.RelationshipTypeConstant;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.service.local.LabelStorageServiceLocal;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.impl.lucene.LuceneIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
public class LabelStorageServiceLocalImpl implements LabelStorageServiceLocal {

	private final GraphStorageTemplate graphStorageTemplate;
	private final Node labelParentNode;
	private final Index<Node> index;
	private ValidationUtility validationUtility;
	private static final Logger logger = LoggerFactory.getLogger(LabelStorageServiceLocalImpl.class);

	public LabelStorageServiceLocalImpl(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
		this.labelParentNode = this.graphStorageTemplate.getParentNode(RelationshipTypeConstant.LABELS);
		this.index = graphStorageTemplate.getIndexManager().forNodes(LABEL_INDEX.name());
		((LuceneIndex<Node>) this.index).setCacheCapacity(LABEL_TEXT_KEY.name(), 300000);
		logger.debug("setting cache for label-text index to 300000");
	}

	@Required
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Override
	@Transactional
	public long createLabel(Label label) {
		validationUtility.validateLabel(label);
		Node node = createLabelNode(label);
		return node.getId();
	}

	public Node createLabelNode(Label label) {
		Node labelNode = graphStorageTemplate.getGraphDatabaseService().createNode();
		labelParentNode.createRelationshipTo(labelNode, RelationshipTypeConstant.LABEL);
		labelNode.setProperty(PropertyConstant.LANGUAGE.name(), label.getLanguage().name());
		labelNode.setProperty(PropertyConstant.TEXT.name(), label.getText());
		index.add(labelNode, LABEL_TEXT_KEY.name(), label.getText());
		return labelNode;
	}

	@Override
	public Label getLabel(long id) {
		validationUtility.validateId(id);
		return new LabelImpl(this.graphStorageTemplate.getGraphDatabaseService().getNodeById(id));
	}

	@Override
	public Collection<Label> getLabelsByText(String text) {
		validationUtility.validateString(text, "text");
		IndexHits<Node> nodes = index.get( LABEL_TEXT_KEY.name(), text);
		Collection<Label> labels = new HashSet<Label>();
		for (Node node : nodes) {
			if (((String) node.getProperty(PropertyConstant.TEXT.name())).equals(text)) {
				labels.add(new LabelImpl(node));
			}
		}
		return labels;

	}

	@Override
	public Node getLabelNode(long id) {
		return graphStorageTemplate.getGraphDatabaseService().getNodeById(id);
	}

	@Override
	public Node getLabelNode(String text, Language language) {
		Node found = null;
		IndexHits<Node> nodes = index.get( LABEL_TEXT_KEY.name(), text);
		try {
			for (Node node : nodes) {
				if (((String) node.getProperty(PropertyConstant.TEXT.name())).equals(text)
						&& language.name().equals((String) node.getProperty(PropertyConstant.LANGUAGE.name()))) {
					found = node;
					break;
				}
			}
		} finally {
			nodes.close();
		}
		return found;
	}

	@Override
	public Collection<Concept> getAllRelatedConceptsForLabelText(String labelText) {
		validationUtility.validateString(labelText, "labelText");
		IndexHits<Node> nodes = index.get( LABEL_TEXT_KEY.name(), labelText);
		Collection<Concept> concepts = new HashSet<Concept>();
		for (Node node : nodes) {
			Iterable<Relationship> rlsps = node
					.getRelationships(RelationshipTypeConstant.HAS_LABEL, Direction.INCOMING);
			for (Relationship relationship : rlsps) {
				concepts.add(new ConceptImpl(relationship.getOtherNode(node)));
			}
		}
		return concepts;
	}
}
