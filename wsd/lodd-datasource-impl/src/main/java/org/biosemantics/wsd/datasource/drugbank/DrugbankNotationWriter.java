//package org.biosemantics.wsd.datasource.drugbank;
//
//import java.util.Set;
//
//import org.biosemantics.conceptstore.domain.Concept;
//import org.biosemantics.conceptstore.domain.Label;
//import org.biosemantics.conceptstore.domain.Notation;
//import org.biosemantics.conceptstore.domain.NotationSourceConstant;
//import org.biosemantics.conceptstore.repository.LabelRepository;
//import org.biosemantics.conceptstore.repository.NotationRepository;
//import org.biosemantics.wsd.datasource.sesame.SesameRepositoryClient;
//import org.neo4j.graphdb.Relationship;
//import org.openrdf.model.Value;
//import org.openrdf.query.BindingSet;
//import org.openrdf.query.MalformedQueryException;
//import org.openrdf.query.QueryEvaluationException;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//import org.openrdf.repository.RepositoryException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.neo4j.support.Neo4jTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//
//@Component
//public class DrugbankNotationWriter {
//
//	@Transactional
//	public void writeAll() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
//		Repository repository = sesameRepositoryClient.getRepository();
//		RepositoryConnection connection = repository.getConnection();
//		try {
//			TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, DRUGBANK_ID_NAME_SPARQL);
//			TupleQueryResult result = tupleQuery.evaluate();
//			while (result.hasNext()) {
//				BindingSet bindingSet = result.next();
//				Value valueOfX = bindingSet.getValue("x");
//				String strDbId = valueOfX.stringValue();
//				String[] split = strDbId.split("/");
//				String dbId = split[split.length - 1];
//				if (dbId.startsWith("DB") && dbId.length() == 7) {
//					Value valueOfY = bindingSet.getValue("y");
//					String drugName = valueOfY.stringValue();
//					Label label = labelRepository.getLabel(drugName, "ENG");
//					Concept found = null;
//					Iterable<Concept> concepts = label.getRelatedConcepts();
//					for (Concept concept : concepts) {
//						
//					}
//					if (concepts != null) {
//						if (concepts.size() == 1) {
//							for (Concept concept : concepts) {
//								Notation notation = null;
//								{
//									notation = notationRepository.getNotation(NotationSourceConstant.DRUGBANK, dbId);
//								}
//								if (notation == null) {
//									notation = notationRepository.save(new Notation(NotationSourceConstant.DRUGBANK,
//											dbId));
//									logger.info("adding dbid {} node", dbId);
//								} else {
//									logger.info("dbid {} node exists", dbId);
//								}
//								Relationship rlsp = null;
//								try {
//									rlsp = neo4jTemplate.getRelationshipBetween(concept, notation, "HAS_NOTATION");
//								} catch (Exception e) {
//								}
//								if (rlsp == null) {
//									concept.addNotationIfNoneExists(neo4jTemplate, notation,
//											NotationSourceConstant.DRUGBANK.toString());
//									logger.info("adding relationship between concept.id {} as dbid", new Object[] {
//											concept.getNodeId(), dbId });
//								} else {
//									logger.info(
//											"relationship exists between concept.id {} and dbid {}. Not creating new rlsp.",
//											new Object[] { concept.getNodeId(), dbId });
//								}
//							}
//						} else {
//							logger.info("{} concepts found for ambiguous drugName {}", new Object[] { concepts.size(),
//									drugName });
//						}
//					}
//
//				} else {
//					logger.error("incorrect dbid = {}", dbId);
//				}
//			}
//
//			result.close();
//
//		} finally {
//			connection.close();
//		}
//	}
//
//	@Autowired
//	private SesameRepositoryClient sesameRepositoryClient;
//	@Autowired
//	private LabelRepository labelRepository;
//	@Autowired
//	private NotationRepository notationRepository;
//	@Autowired
//	private Neo4jTemplate neo4jTemplate;
//
//	private static final Logger logger = LoggerFactory.getLogger(DrugbankNotationWriter.class);
//
//	private static final String DRUGBANK_ID_NAME_SPARQL = "select ?x ?y from <file://C:/fakepath/drugbank_dump.nt> where {?x <http://www.w3.org/2000/01/rdf-schema#label> ?y}";
//
//}
