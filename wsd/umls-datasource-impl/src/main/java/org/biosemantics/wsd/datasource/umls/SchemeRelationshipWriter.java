package org.biosemantics.wsd.datasource.umls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.biosemantics.wsd.domain.Concept;
import org.biosemantics.wsd.domain.Notation;
import org.biosemantics.wsd.repository.NotationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

public class SchemeRelationshipWriter {
	private static final String IS_A = "T186";
	@Autowired
	private DataSource dataSource;
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private NotationRepository notationRepository;
	private static final String GET_ALL_PREDICATES = "SELECT UI, STY_RL FROM SRDEF WHERE RT=\"RL\"";// 54
	private static final String GET_ALL_SCHEME_RLSP = "SELECT * FROM SRSTRE1 WHERE UI1 != UI3";// 6483
	private static final Logger logger = LoggerFactory.getLogger(SchemeRelationshipWriter.class);
	private static final String UMLS_SRSTRE1 = "UMLS_2011_AA | SRSTRE1";
	private Map<String, String> duplicateHierarchicalCache = new HashMap<String, String>();
	private Map<String, String> duplicateRelatedCache = new HashMap<String, String>();
	private Map<String, String> predicateCache = new HashMap<String, String>();

	@Transactional
	public void writeAll() throws SQLException {
		savePredicatesToCache();
		writeConceptSchemeRlsp();
	}

	private void writeConceptSchemeRlsp() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement();
		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_SCHEME_RLSP);
			int ctr = 0;
			while (rs.next()) {
				String ui1 = rs.getString("UI1");
				String ui2 = rs.getString("UI2");
				String ui3 = rs.getString("UI3");

				if (ui2.equalsIgnoreCase(IS_A)) {
					if (duplicateHierarchicalCache.containsKey(ui1 + ui3)
							|| duplicateHierarchicalCache.containsKey(ui3 + ui1)) {
						// ignore
					} else {
						Notation notation = notationRepository.findByPropertyValue("code", ui1);
						if(notation == null){
							continue;
						}
						Concept concept = notationRepository.getRelatedConcept(notation);
						Notation otherNotation = notationRepository.findByPropertyValue("code", ui3);
						if(otherNotation == null){
							continue;
						}
						Concept otherConcept = notationRepository.getRelatedConcept(otherNotation);
						if (concept != null && otherConcept != null) {
							String predicate = predicateCache.get(ui2);
							concept.hasChild(neo4jTemplate, otherConcept, 100, predicate, UMLS_SRSTRE1);
							// add to cache to avoid duplicates
							ctr++;
							duplicateHierarchicalCache.put(ui1 + ui3, null);
						}
					}
				} else {
					if (duplicateRelatedCache.containsKey(ui1 + ui3) || duplicateRelatedCache.containsKey(ui3 + ui1)) {
						// ignore
					} else {
						Notation notation = notationRepository.findByPropertyValue("code", ui1);
						Concept concept = notationRepository.getRelatedConcept(notation);
						Notation otherNotation = notationRepository.findByPropertyValue("code", ui3);
						Concept otherConcept = notationRepository.getRelatedConcept(otherNotation);
						if (concept != null && otherConcept != null) {
							String predicate = predicateCache.get(ui2);
							concept.relatedTo(neo4jTemplate, otherConcept, 100, predicate, UMLS_SRSTRE1);
							ctr++;
							// add to cache to avoid duplicates
							duplicateRelatedCache.put(ui1 + ui3, null);
						}
					}

				}
			}
			logger.info("{} concept sceme rlsp's created", ctr);
			rs.close();
		} finally {
			stmt.close();
			connection.close();
			duplicateHierarchicalCache.clear();
			duplicateRelatedCache.clear();
			predicateCache.clear();
		}

	}

	private void savePredicatesToCache() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement();
		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_PREDICATES);
			while (rs.next()) {
				String ui = rs.getString("UI");
				String sty_rl = rs.getString("STY_RL");
				predicateCache.put(ui, sty_rl);
			}
			logger.info("{} predicates cached", predicateCache.size());
			rs.close();
		} finally {
			stmt.close();
			connection.close();
		}

	}

}
