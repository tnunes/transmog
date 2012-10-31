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
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;

public class RelationshipWriter {

	public void setIgnoredCuiReader(IgnoredCuiReader ignoredCuiReader) {
		this.ignoredCuiReader = ignoredCuiReader;
	}

	public void writeAll() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		Transaction tx = neo4jTemplate.getGraphDatabaseService().beginTx();
		try {
			ResultSet rs = stmt.executeQuery(GET_ALL_FACTUAL_RLSP_SQL);
			int ctr = 0;

			while (rs.next()) {
				ctr++;
				String cui1 = rs.getString("CUI1");
				String cui2 = rs.getString("CUI2");
				if (ignoredCuiReader.isIgnored(cui1) || ignoredCuiReader.isIgnored(cui2)) {
					continue;
				}
				String rel = rs.getString("REL");
				String rela = rs.getString("RELA");
				String relUpper = rel.toUpperCase().trim();
				if (relUpper.equals("CHD") || relUpper.equals("RB") || relUpper.equals("RO") || relUpper.equals("SIB")
						|| relUpper.equals("SY")) {
					// Find all relationships for a UMLS concept. Note: In
					// MRREL, the REL/RELA always expresses the
					// nature of the relationship from CUI2 to the
					// "current concept", CUI1. Because we're querying
					// CUI1 below, this represents the "natural" direction
					// of the relationship.
					if (relUpper.equals("CHD") || relUpper.equals("RB")) {
						if (duplicateHierarchicalCache.containsKey(cui1 + cui2)
								|| duplicateHierarchicalCache.containsKey(cui2 + cui1)) {
							// rlsp exists do nothing
						} else {
							Notation notation = notationRepository.findByPropertyValue("code", cui2);
							Concept concept = notationRepository.getRelatedConcept(notation);
							Notation otherNotation = notationRepository.findByPropertyValue("code", cui1);
							Concept otherConcept = notationRepository.getRelatedConcept(otherNotation);
							concept.hasChild(neo4jTemplate, otherConcept, 100, rela, RELATIONSHIP_SOURCE);
							duplicateHierarchicalCache.put(cui1 + cui2, null);
						}
					} else {
						if (duplicateRelatedCache.containsKey(cui1 + cui2)
								|| duplicateRelatedCache.containsKey(cui2 + cui1)) {
							// rlsp exists do nothing
						} else {
							Notation notation = notationRepository.findByPropertyValue("code", cui2);
							Concept concept = notationRepository.getRelatedConcept(notation);
							Notation otherNotation = notationRepository.findByPropertyValue("code", cui1);
							Concept otherConcept = notationRepository.getRelatedConcept(otherNotation);
							concept.relatedTo(neo4jTemplate, otherConcept, 100, rela, RELATIONSHIP_SOURCE);
							duplicateRelatedCache.put(cui1 + cui2, null);
						}
					}
				} else {
					// logger.error("REL  = {} received. Illegal value for relationships.",
					// rel);
					throw new IllegalArgumentException("Illegal value for relationships." + rel);
				}
				// 1 million
				if (ctr % 1000000 == 0) {
					tx.success();
					tx.finish();
					tx = neo4jTemplate.getGraphDatabaseService().beginTx();
					logger.info("ctr:{}", ctr);
				}
			}
			rs.close();
		} finally {
			stmt.close();
			connection.close();
			tx.success();
			tx.finish();
			duplicateHierarchicalCache.clear();
			duplicateRelatedCache.clear();
		}

	}

	private static final String RELATIONSHIP_SOURCE = "UMLS_2011AA_MRREL";
	@Autowired
	private DataSource dataSource;
	@Autowired
	private Neo4jTemplate neo4jTemplate;
	@Autowired
	private NotationRepository notationRepository;
	private IgnoredCuiReader ignoredCuiReader;
	private static final String GET_ALL_FACTUAL_RLSP_SQL = "select CUI1, CUI2, REL, RELA from MRREL where CUI1 != CUI2 AND REL IN ('CHD', 'RB', 'RO', 'SIB', 'SY')";// 14953305
	private static final Logger logger = LoggerFactory.getLogger(RelationshipWriter.class);
	private Map<String, String> duplicateHierarchicalCache = new HashMap<String, String>();
	private Map<String, String> duplicateRelatedCache = new HashMap<String, String>();
}
