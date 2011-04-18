package org.biosemantics.disambiguation.script.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.disambiguation.domain.impl.ConceptImpl;
import org.biosemantics.disambiguation.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.biosemantics.disambiguation.script.AmbiguousLabelScript;
import org.biosemantics.disambiguation.script.OutputSink;
import org.biosemantics.disambiguation.service.local.impl.DefaultRelationshipType;
import org.biosemantics.disambiguation.service.local.impl.GraphStorageTemplate;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class AmbiguousLabelScriptImpl implements AmbiguousLabelScript {

	private static final Logger logger = LoggerFactory.getLogger(AmbiguousLabelScriptImpl.class);
	private GraphStorageTemplate graphStorageTemplate;
	private OutputSink outputSink;

	@Required
	@Override
	public void setOutputSink(OutputSink outputSink) {
		this.outputSink = outputSink;
	}

	@Required
	public void setGraphStorageTemplate(GraphStorageTemplate graphStorageTemplate) {
		this.graphStorageTemplate = graphStorageTemplate;
	}

	@Override
	public void writeAmbiguousLabels() {
		Node labelParentNode = graphStorageTemplate.getParentNode(DefaultRelationshipType.LABELS);
		Iterable<Relationship> relationships = labelParentNode.getRelationships(DefaultRelationshipType.LABEL,
				Direction.OUTGOING);
		logger.info("starting all labels traversal");
		int totalLabelCounter = 0;
		for (Relationship relationship : relationships) {
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			Node labelNode = relationship.getOtherNode(labelParentNode);
			// for each label
			Iterable<Relationship> labelRelationships = labelNode.getRelationships(DefaultRelationshipType.HAS_LABEL,
					Direction.INCOMING);
			List<String> conceptDetails = new ArrayList<String>();
			for (Relationship labelRelationship : labelRelationships) {
				Node conceptNode = labelRelationship.getOtherNode(labelNode);
				StringBuilder conceptDetail = new StringBuilder(
						(String) conceptNode.getProperty(ConceptImpl.UUID_PROPERTY)).append("|").append(
						getPreferredLabelText(conceptNode));
				conceptDetails.add(conceptDetail.toString());
			}
			AmbiguousLabelOutputObject object = new AmbiguousLabelOutputObject(new LabelImpl(labelNode), conceptDetails);
			outputSink.write(object);
			stopWatch.stop();
			++totalLabelCounter;
			if (stopWatch.getTime() > 5) {
				logger.debug("time taken to process label number {} is {}(ms)", new Object[] { totalLabelCounter,
						stopWatch.getTime() });
			}
		}
	}

	private String getPreferredLabelText(Node conceptNode) {
		String label = "";
		Iterable<Relationship> relationships = conceptNode.getRelationships(DefaultRelationshipType.HAS_LABEL);

		for (Relationship relationship : relationships) {
			if (relationship.getProperty(ConceptImpl.LABEL_TYPE_RLSP_PROPERTY).equals(LabelType.PREFERRED.name())
					&& ((String) relationship.getOtherNode(conceptNode).getProperty(LabelImpl.LANGUAGE_PROPERTY))
							.equals(LanguageImpl.EN.name())) {
				label = (String) relationship.getOtherNode(conceptNode).getProperty(LabelImpl.TEXT_PROPERTY);
				break;
			}
		}
		return label;
	}

}
