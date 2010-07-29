package org.biosemantics.disambiguation.knowledgebase.tools.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.biosemantics.disambiguation.knowledgebase.api.Concept;
import org.biosemantics.disambiguation.knowledgebase.api.Label;
import org.biosemantics.disambiguation.knowledgebase.neo4j.impl.ConceptImpl;
import org.biosemantics.disambiguation.knowledgebase.neo4j.impl.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.neo4j.impl.LabelImpl;
import org.biosemantics.disambiguation.knowledgebase.tools.AmbiguousLabelLocator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVWriter;

public class AmbiguousLabelLocatorFileImpl implements AmbiguousLabelLocator {

	public enum FileType {
		CSV(',', ".csv"), TSV('\t', ".tsv");
		private char fileSeparator;
		private String fileExtension;

		private FileType(char fileSeparator, String fileExtension) {
			this.fileSeparator = fileSeparator;
			this.fileExtension = fileExtension;
		}
	}

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AmbiguousLabelLocatorFileImpl.class);
	private final GraphDatabaseService graphDb;
	private final FileType fileType;
	private File outputFile;
	private String outputFolder;
	private int logInterval = 10000;

	public AmbiguousLabelLocatorFileImpl(GraphDatabaseService graphDatabaseService, FileType fileType)
			throws IOException {
		if (graphDatabaseService == null)
			throw new NullArgumentException("graphDatabaseService");
		this.graphDb = graphDatabaseService;
		if (fileType == null)
			throw new NullArgumentException("fileType");
		this.fileType = fileType;
	}

	public void setLogInterval(int logInterval) {
		this.logInterval = logInterval;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	@Override
	@Transactional
	public void locateAmbiguousLabels() {
		CSVWriter writer = null;
		try {
			Node labelFactoryNode;
			Relationship relationship = graphDb.getReferenceNode().getSingleRelationship(
					KnowledgebaseRelationshipType.LABELS, Direction.OUTGOING);
			if (relationship == null) {
				logger.warn(
						"no {} relationship found. If zero labels exist there can be no ambiguous labels. Exiting now.",
						KnowledgebaseRelationshipType.LABELS);
			} else {
				labelFactoryNode = relationship.getEndNode();
				Traverser labelNodes = labelFactoryNode
						.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
								KnowledgebaseRelationshipType.LABEL, Direction.OUTGOING);
				if (StringUtils.isBlank(outputFolder)) {
					outputFile = File
							.createTempFile(String.valueOf(System.currentTimeMillis()), fileType.fileExtension);
				} else {
					outputFile = new File(outputFolder, System.currentTimeMillis() + fileType.fileExtension);
				}
				logger.info("output file path \"{}\"", outputFile.getAbsolutePath());
				writer = new CSVWriter(new FileWriter(outputFile), fileType.fileSeparator);
				long labelNodeCounter = 0;
				logger.info("{} root node found, ambiguous label search starts", KnowledgebaseRelationshipType.LABELS);
				for (Node labelNode : labelNodes) {
					long startTime = System.currentTimeMillis();
					Traverser conceptNodes = labelNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
							ReturnableEvaluator.ALL_BUT_START_NODE, KnowledgebaseRelationshipType.HAS_LABEL,
							Direction.INCOMING);
					List<Node> conceptNodeList = new ArrayList<Node>();
					for (Node conceptNode : conceptNodes) {
						conceptNodeList.add(conceptNode);
					}
					logger.debug("{} concepts found for label {}",
							new Object[] { conceptNodeList.size(), labelNode.getProperty("id") });
					if (conceptNodeList.size() > 1) {
						Label label = new LabelImpl(labelNode);
						// csv columns like "lableId","10", "conceptid"...
						List<String> csvColumns = new ArrayList<String>();
						csvColumns.add(label.getId());
						csvColumns.add(String.valueOf(conceptNodeList.size()));
						for (Node node : conceptNodeList) {
							Concept concept = new ConceptImpl(node);
							csvColumns.add(concept.getId());
						}
						// write line to file
						writer.writeNext(csvColumns.toArray(new String[csvColumns.size()]));
						writer.flush();
					}
					labelNodeCounter++;
					if (labelNodeCounter % logInterval == 0) {
						long endTime = System.currentTimeMillis();
						logger.info("completed iterating {} label nodes in {} (ms)", new Object[] { labelNodeCounter,
								(endTime - startTime) });
						startTime = endTime;
					}
				}
				logger.info("ambiguous label search ends. See output file for results.");
			}
		} catch (IOException e) {
			logger.error("IOexception when creating log file", e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					logger.warn("umable to close file writer properly. Potential memory leak", e);
				}
		}
	}
}
