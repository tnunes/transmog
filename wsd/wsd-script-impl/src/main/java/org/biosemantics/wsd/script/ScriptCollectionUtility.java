package org.biosemantics.wsd.script;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class ScriptCollectionUtility {

	@Autowired
	private Neo4jTemplate neo4jTemplate;

	public List<Node> getAllChildNodes(long nodeId) {
		List<Node> nodes = new ArrayList<Node>();
		Node node = neo4jTemplate.getNode(nodeId);
		TraversalDescription td = Traversal.description().depthFirst().relationships(new RelationshipType() {
			@Override
			public String name() {
				return "CHILD";
			}
		}, Direction.OUTGOING).evaluator(Evaluators.toDepth(1));
		Traverser traverser = td.traverse(node);
		for (Path path : traverser) {
			System.out.println(path.length());
			nodes.add(path.endNode());
		}
		return nodes;
	}

}
