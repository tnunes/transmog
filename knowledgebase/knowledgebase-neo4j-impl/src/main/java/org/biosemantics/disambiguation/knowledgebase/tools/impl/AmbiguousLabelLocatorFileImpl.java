package org.biosemantics.disambiguation.knowledgebase.tools.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.biosemantics.disambiguation.knowledgebase.service.Concept;
import org.biosemantics.disambiguation.knowledgebase.service.Label;
import org.biosemantics.disambiguation.knowledgebase.service.impl.ConceptImpl;
import org.biosemantics.disambiguation.knowledgebase.service.impl.KnowledgebaseRelationshipType;
import org.biosemantics.disambiguation.knowledgebase.service.impl.LabelImpl;
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

import au.com.bytecode.opencsv.CSVWriter;

public class AmbiguousLabelLocatorFileImpl implements AmbiguousLabelLocator {

	public enum FileType {
		CSV(',', "csv"), TSV('\t', "tsv");
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
	private int logInterval = 1000;

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

	@Override
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
				outputFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), fileType.fileExtension);
				writer = new CSVWriter(new FileWriter(outputFile), fileType.fileSeparator);
				logger.info("absolute path for output file is {}", outputFile.getAbsolutePath());
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
					if (conceptNodeList.size() > 1) {
						Label label = new LabelImpl(labelNode);
						List<String> columns = new ArrayList<String>();
						columns.add(label.getId());
						columns.add(String.valueOf(conceptNodeList.size()));
						for (Node node : conceptNodeList) {
							Concept concept = new ConceptImpl(node);
							columns.add(concept.getId());
						}
						// write line to file
						writer.writeNext(columns.toArray(new String[columns.size()]));
						writer.flush();
						labelNodeCounter++;
						if (labelNodeCounter % logInterval == 0) {
							logger.info("completed iterating {} label nodes in {} (ms)", new Object[] {
									labelNodeCounter, (System.currentTimeMillis() - startTime) });
							startTime = System.currentTimeMillis();
						}
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
