package org.biosemantics.conceptstore.repository.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.biosemantics.conceptstore.domain.Concept;
import org.biosemantics.conceptstore.domain.HasLabel;
import org.biosemantics.conceptstore.domain.HasNotation;
import org.biosemantics.conceptstore.domain.HasRlsp;
import org.biosemantics.conceptstore.domain.InScheme;
import org.biosemantics.conceptstore.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.domain.impl.ConceptType;
import org.biosemantics.conceptstore.domain.impl.HasLabelImpl;
import org.biosemantics.conceptstore.domain.impl.HasNotationImpl;
import org.biosemantics.conceptstore.domain.impl.HasRlspImpl;
import org.biosemantics.conceptstore.domain.impl.InSchemeImpl;
import org.biosemantics.conceptstore.domain.impl.LabelType;
import org.biosemantics.conceptstore.domain.impl.RlspType;
import org.biosemantics.conceptstore.repository.ConceptRepository;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptRepositoryImpl implements ConceptRepository {

	public ConceptRepositoryImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDb = graphDatabaseService;
		conceptNodeIndex = this.graphDb.index().forNodes("Concept");
	}

	// 402 - 410
	@Override
	public Collection<Long> getAllChildPredicates(Long predicateConceptId) {
		Node node = graphDb.getNodeById(predicateConceptId);
		if (node == null) {
			throw new IllegalArgumentException("no node found with id " + predicateConceptId);
		}
		TraversalDescription td = Traversal.description().depthFirst().relationships(IS_A_RLSP, Direction.INCOMING)
				.evaluator(Evaluators.excludeStartPosition());
		Set<Long> ids = new HashSet<Long>();
		for (Path path : td.traverse(node)) {
			ids.add(path.endNode().getId());
		}
		return ids;
	}

	@Override
	public Collection<HasRlsp> getAllHasRlspsForConcept(long id) {
		Set<HasRlsp> hasRlspsImpls = new HashSet<HasRlsp>();
		Node node = graphDb.getNodeById(id);
		if (node == null) {
			throw new IllegalArgumentException("no node found with id " + id);
		}
		Iterable<Relationship> relationships = node.getRelationships();
		for (Relationship relationship : relationships) {
			boolean add = true;
			for (RlspType rlspType : RlspType.values()) {
				if (rlspType.toString().equals(relationship.getType().name())) {
					add = false;
					break;
				}
			}
			if (add) {
				hasRlspsImpls.add(new HasRlspImpl(relationship));
			}
		}
		return hasRlspsImpls;
	}

	@Override
	public InScheme addInScheme(long conceptId, long conceptSchemeId, String... sources) {
		Transaction tx = graphDb.beginTx();
		try {
			Node conceptNode = graphDb.getNodeById(conceptId);
			Node conceptSchemeNode = graphDb.getNodeById(conceptSchemeId);
			Relationship relationship = conceptNode.createRelationshipTo(conceptSchemeNode, RlspType.IN_SCHEME);
			relationship.setProperty("sources", sources);
			return new InSchemeImpl(relationship);
		} finally {
			tx.success();
			tx.finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.ConceptRepository#create
	 * (org.biosemantics.conceptstore.domain.impl.ConceptType)
	 */
	@Override
	public Concept create(ConceptType conceptType) {
		Transaction tx = graphDb.beginTx();
		try {
			Node node = graphDb.createNode();
			node.setProperty("type", conceptType.toString());
			conceptNodeIndex.add(node, "type", conceptType.toString());
			return new ConceptImpl(node);
		} finally {
			tx.success();
			tx.finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.ConceptRepository#hasLabel
	 * (long, long, org.biosemantics.conceptstore.domain.impl.LabelType,
	 * java.lang.String)
	 */
	@Override
	public HasLabel hasLabel(long conceptId, long labelId, LabelType labelType, String... sources) {
		Transaction tx = graphDb.beginTx();
		try {
			Node conceptNode = graphDb.getNodeById(conceptId);
			Node labelNode = graphDb.getNodeById(labelId);
			Relationship relationship = conceptNode.createRelationshipTo(labelNode, RlspType.HAS_LABEL);
			relationship.setProperty("type", labelType.toString());
			relationship.setProperty("sources", sources);
			return new HasLabelImpl(relationship);
		} finally {
			tx.success();
			tx.finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.repository.impl.ConceptRepository#
	 * hasLabelIfNoneExists(long, long,
	 * org.biosemantics.conceptstore.domain.impl.LabelType, java.lang.String)
	 */
	@Override
	public HasLabel hasLabelIfNoneExists(long conceptId, long labelId, LabelType labelType, String... sources) {
		Node conceptNode = graphDb.getNodeById(conceptId);
		Node labelNode = graphDb.getNodeById(labelId);
		Relationship foundRelationship = null;
		Iterable<Relationship> foundRelationships = conceptNode
				.getRelationships(RlspType.HAS_LABEL, Direction.OUTGOING);
		for (Relationship relationship : foundRelationships) {
			Node endNode = relationship.getEndNode();
			if (endNode.equals(labelNode)) {
				foundRelationship = relationship;
				break;
			}
		}
		if (foundRelationship == null) {
			Transaction tx = graphDb.beginTx();
			try {
				Relationship relationship = conceptNode.createRelationshipTo(labelNode, RlspType.HAS_LABEL);
				relationship.setProperty("type", labelType.toString());
				relationship.setProperty("sources", sources);
				return new HasLabelImpl(relationship);
			} finally {
				tx.success();
				tx.finish();
			}
		} else {
			return new HasLabelImpl(foundRelationship);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.ConceptRepository#hasNotation
	 * (long, long, java.lang.String)
	 */
	@Override
	public HasNotation hasNotation(long conceptId, long notationId, String... sources) {
		Transaction tx = graphDb.beginTx();
		try {
			Node conceptNode = graphDb.getNodeById(conceptId);
			Node notationNode = graphDb.getNodeById(notationId);
			Relationship relationship = conceptNode.createRelationshipTo(notationNode, RlspType.HAS_NOTATION);
			relationship.setProperty("sources", sources);
			return new HasNotationImpl(relationship);
		} finally {
			tx.success();
			tx.finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.repository.impl.ConceptRepository#
	 * hasNotationIfNoneExists(long, long, java.lang.String)
	 */
	@Override
	public HasNotation hasNotationIfNoneExists(long conceptId, long notationId, String... sources) {
		Node conceptNode = graphDb.getNodeById(conceptId);
		Node notationNode = graphDb.getNodeById(notationId);
		Relationship foundRelationship = null;
		Iterable<Relationship> foundRelationships = conceptNode.getRelationships(RlspType.HAS_NOTATION,
				Direction.OUTGOING);
		for (Relationship relationship : foundRelationships) {
			Node endNode = relationship.getEndNode();
			if (endNode.equals(notationNode)) {
				foundRelationship = relationship;
				break;
			}
		}
		if (foundRelationship == null) {
			Transaction tx = graphDb.beginTx();
			try {
				Relationship relationship = conceptNode.createRelationshipTo(notationNode, RlspType.HAS_NOTATION);
				relationship.setProperty("sources", sources);
				return new HasNotationImpl(relationship);
			} finally {
				tx.success();
				tx.finish();
			}
		} else {
			return new HasNotationImpl(foundRelationship);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.ConceptRepository#hasRlsp
	 * (long, long, java.lang.String, java.lang.String)
	 */
	@Override
	public HasRlsp hasRlsp(long fromConceptId, long toConceptId, String relationshipType, String... sources) {
		Transaction tx = graphDb.beginTx();
		try {
			Node fromNode = graphDb.getNodeById(fromConceptId);
			Node toNode = graphDb.getNodeById(toConceptId);
			Relationship relationship = fromNode.createRelationshipTo(toNode,
					DynamicRelationshipType.withName(relationshipType));
			relationship.setProperty("sources", sources);
			return new HasRlspImpl(relationship);
		} finally {
			tx.success();
			tx.finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.repository.impl.ConceptRepository#
	 * hasRlspIfNoneExists(long, long, java.lang.String, java.lang.String)
	 */
	@Override
	public HasRlsp hasRlspIfNoneExists(long fromConceptId, long toConceptId, String relationshipType, String... sources) {
		Node fromNode = graphDb.getNodeById(fromConceptId);
		Node toNode = graphDb.getNodeById(toConceptId);
		Relationship foundRelationship = null;
		Iterable<Relationship> foundRelationships = fromNode.getRelationships(
				DynamicRelationshipType.withName(relationshipType), Direction.OUTGOING);
		for (Relationship relationship : foundRelationships) {
			Node endNode = relationship.getEndNode();
			if (endNode.equals(toNode)) {
				foundRelationship = relationship;
				break;
			}
		}
		if (foundRelationship == null) {
			Transaction tx = graphDb.beginTx();
			try {
				Relationship relationship = fromNode.createRelationshipTo(toNode,
						DynamicRelationshipType.withName(relationshipType));
				relationship.setProperty("sources", sources);
				return new HasRlspImpl(relationship);
			} finally {
				tx.success();
				tx.finish();
			}
		} else {
			return new HasRlspImpl(foundRelationship);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.biosemantics.conceptstore.repository.impl.ConceptRepository#
	 * hasRlspIfNoBidirectionalRlspExists(long, long, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public HasRlsp hasRlspIfNoBidirectionalRlspExists(long fromConceptId, long toConceptId, String relationshipType,
			String... sources) {
		Node fromNode = graphDb.getNodeById(fromConceptId);
		Node toNode = graphDb.getNodeById(toConceptId);
		Relationship foundOneDirectionRelationship = null;
		Iterable<Relationship> foundRelationships = fromNode.getRelationships(
				DynamicRelationshipType.withName(relationshipType), Direction.OUTGOING);
		for (Relationship relationship : foundRelationships) {
			Node endNode = relationship.getEndNode();
			if (endNode.equals(toNode)) {
				foundOneDirectionRelationship = relationship;
				break;
			}
		}
		Relationship foundOtherDirectionRelationship = null;
		if (foundOneDirectionRelationship == null) {
			Iterable<Relationship> foundOtherDirRelationships = toNode.getRelationships(
					DynamicRelationshipType.withName(relationshipType), Direction.OUTGOING);
			for (Relationship relationship : foundOtherDirRelationships) {
				Node endNode = relationship.getEndNode();
				if (endNode.equals(fromNode)) {
					foundOtherDirectionRelationship = relationship;
					break;
				}
			}
		}
		if (foundOneDirectionRelationship == null && foundOtherDirectionRelationship == null) {
			Transaction tx = graphDb.beginTx();
			try {
				Relationship relationship = fromNode.createRelationshipTo(toNode,
						DynamicRelationshipType.withName(relationshipType));
				relationship.setProperty("sources", sources);
				return new HasRlspImpl(relationship);
			} finally {
				tx.success();
				tx.finish();
			}
		} else {
			if (foundOneDirectionRelationship != null) {
				return new HasRlspImpl(foundOneDirectionRelationship);
			} else {
				return new HasRlspImpl(foundOtherDirectionRelationship);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.ConceptRepository#getById
	 * (long)
	 */
	@Override
	public Concept getById(long id) {
		Node node = graphDb.getNodeById(id);
		if (node == null) {
			throw new IllegalArgumentException("no node for id = " + id);
		} else {
			return new ConceptImpl(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.biosemantics.conceptstore.repository.impl.ConceptRepository#getByType
	 * (org.biosemantics.conceptstore.domain.impl.ConceptType)
	 */
	@Override
	public Collection<Concept> getByType(ConceptType conceptType) {
		IndexHits<Node> nodes = conceptNodeIndex.get("type", conceptType.toString());
		Set<Concept> concepts = new HashSet<Concept>();
		for (Node node : nodes) {
			concepts.add(new ConceptImpl(node));
		}
		return concepts;
	}

	private GraphDatabaseService graphDb;
	private Index<Node> conceptNodeIndex;
	private static final Logger logger = LoggerFactory.getLogger(ConceptRepositoryImpl.class);
	private static final RelationshipType IS_A_RLSP = DynamicRelationshipType.withName("578");

}
