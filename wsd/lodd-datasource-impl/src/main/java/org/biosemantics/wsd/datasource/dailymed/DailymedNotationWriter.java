//package org.biosemantics.wsd.datasource.dailymed;
//
//import org.biosemantics.conceptstore.domain.Concept;
//import org.biosemantics.conceptstore.domain.Notation;
//import org.biosemantics.conceptstore.domain.NotationSourceConstant;
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
//
//@Component
//public class DailymedNotationWriter {
//
//	@Transactional
//	public void writeAll() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
//		Repository repository = sesameRepositoryClient.getRepository();
//		RepositoryConnection connection = repository.getConnection();
//		try {
//			TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, DAILYMED_DRUGBANK_ID_SPARQL);
//			TupleQueryResult result = tupleQuery.evaluate();
//			while (result.hasNext()) {
//				BindingSet bindingSet = result.next();
//				Value valueOfY = bindingSet.getValue("y");
//				String strDbId = valueOfY.stringValue();
//				String[] split = strDbId.split("/");
//				String drugbankId = split[split.length - 1];
//				if (drugbankId.startsWith("DB") && drugbankId.length() == 7) {
//					Notation drugbankNotation = null;
//					try {
//						drugbankNotation = notationRepository.getNotation(NotationSourceConstant.DRUGBANK.toString(),
//								drugbankId);
//						Iterable<Concept> concepts = drugbankNotation.getRelatedConcepts();
//						Value valueOfX = bindingSet.getValue("x");
//						String strDailymedId = valueOfX.stringValue();
//						String[] strings = strDailymedId.split("/");
//						int dailymedId = Integer.parseInt(strings[strings.length - 1]);
//						Notation dailymedNotation = null;
//						try {
//							dailymedNotation = notationRepository.getNotation(
//									NotationSourceConstant.DAILYMED.toString(), "" + dailymedId);
//						} catch (NullPointerException e) {
//						}
//						if (dailymedNotation == null) {
//							dailymedNotation = notationRepository.save(new Notation(NotationSourceConstant.DAILYMED
//									.toString(), "" + dailymedId));
//						}
//						for (Concept concept : concepts) {
//							Relationship rlsp = null;
//							try {
//								rlsp = neo4jTemplate.getRelationshipBetween(concept, dailymedNotation, "HAS_NOTATION");
//							} catch (Exception e) {
//							}
//							if (rlsp == null) {
//								concept.addNotationIfNoneExists(neo4jTemplate, dailymedNotation,
//										NotationSourceConstant.DAILYMED.toString());
//								logger.info("adding relationship between concept.id {} as dailymedId {}", new Object[] {
//										concept.getNodeId(), dailymedId });
//							} else {
//								logger.info(
//										"relationship exists between concept.id {} and dailymedId {}. Not creating new rlsp.",
//										new Object[] { concept.getNodeId(), dailymedId });
//							}
//						}
//					} catch (Exception e) {
//						logger.info("Exception:", e);
//					}
//				}
//			}
//			result.close();
//		} finally {
//			connection.close();
//		}
//	}
//
//	@Autowired
//	private SesameRepositoryClient sesameRepositoryClient;
//	@Autowired
//	private NotationRepository notationRepository;
//	@Autowired
//	private Neo4jTemplate neo4jTemplate;
//
//	private static final Logger logger = LoggerFactory.getLogger(DailymedNotationWriter.class);
//
//	private static final String DAILYMED_DRUGBANK_ID_SPARQL = "select ?x ?y where {?x <http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/genericDrug> ?y}";
//
// }
