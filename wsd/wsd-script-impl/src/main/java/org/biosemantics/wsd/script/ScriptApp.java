package org.biosemantics.wsd.script;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import au.com.bytecode.opencsv.CSVWriter;

public class ScriptApp {

	private static final String[] contexts = new String[] { "script-run-context.xml" };

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(contexts);
		applicationContext.registerShutdownHook();
		ScriptCollectionUtility scriptCollectionUtility = applicationContext.getBean(ScriptCollectionUtility.class);
		CSVWriter csvWriter = new CSVWriter(new FileWriter("/Users/bhsingh/Desktop/administration.csv"));
		List<Node> nodes = scriptCollectionUtility.getAllChildNodes(13005);
		System.out.println("no of nodes = " + nodes.size());
		for (Node node : nodes) {
			String id = (String) node.getProperty("id");
			Iterable<Relationship> rlsps = node.getRelationships(new RelationshipType() {
				@Override
				public String name() {
					// TODO Auto-generated method stub
					return "HAS_NOTATION";
				}
			}, Direction.OUTGOING);
			for (Relationship relationship : rlsps) {
				String code = (String) relationship.getOtherNode(node).getProperty("code");
				if (code.startsWith("C")) {
					csvWriter.writeNext(new String[] { id, code });
				}
			}
		}
		csvWriter.flush();
		csvWriter.close();

	}

}
