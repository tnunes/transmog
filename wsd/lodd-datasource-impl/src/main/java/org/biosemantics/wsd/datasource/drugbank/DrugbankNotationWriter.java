package org.biosemantics.wsd.datasource.drugbank;

import java.util.Set;

import org.biosemantics.wsd.datasource.sesame.SesameRepositoryClient;
import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Label;
import org.biosemantics.wsd.domain.Notation;
import org.biosemantics.wsd.domain.NotationSourceConstant;
import org.biosemantics.wsd.repository.LabelRepository;
import org.biosemantics.wsd.repository.NotationRepository;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

public class DrugbankNotationWriter {

	@Transactional
	public void writeAll() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		Repository repository = sesameRepositoryClient.getRepository();
		RepositoryConnection connection = repository.getConnection();
		try {
			TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, DRUGBANK_ID_NAME_SPARQL);
			TupleQueryResult result = tupleQuery.evaluate();
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value valueOfX = bindingSet.getValue("x");
				String strDbId = valueOfX.stringValue();
				String[] split = strDbId.split("/");
				String dbId = split[split.length - 1];
				Value valueOfY = bindingSet.getValue("y");
				String drugName = valueOfY.stringValue();
				Label label = labelRepository.getLabel(drugName, "ENG");
				Set<Concept> concepts = label.getRelatedConcepts();
				if (concepts.size() == 1) {
					for (Concept concept : concepts) {
						Notation notation = notationRepository
								.save(new Notation(NotationSourceConstant.DRUGBANK, dbId));
						concept.hasNotation(neo4jTemplate, notation, NotationSourceConstant.DRUGBANK);
					}
				} else {
					logger.info("{} concepts found for drugName {}", new Object[] { concepts.size(), drugName });
				}
			}
			result.close();

		} finally {
			connection.close();
		}
	}

	@Autowired
	private SesameRepositoryClient sesameRepositoryClient;
	@Autowired
	private LabelRepository labelRepository;
	@Autowired
	private NotationRepository notationRepository;
	@Autowired
	private Neo4jTemplate neo4jTemplate;

	private static final Logger logger = LoggerFactory.getLogger(DrugbankNotationWriter.class);

	private static final String DRUGBANK_ID_NAME_SPARQL = "select ?x ?y from <file://C:/fakepath/drugbank_dump.nt> where {?x <http://www.w3.org/2000/01/rdf-schema#label> ?y }";

}
