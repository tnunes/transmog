package org.biosemantics.datasource.umls.relationship;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.datasource.umls.cache.UmlsCacheService;
import org.biosemantics.datasource.umls.concept.UmlsUtils;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ConceptToSchemeRlspWriter {

	private BulkImportService bulkImportService;
	private UmlsCacheService umlsCacheService;
	private DataSource dataSource;
	private Connection connection;
	private Statement statement;
	private static final String GET_CONCEPT_TO_SCHEME_RELATIONS_SQL = "SELECT CUI, STY FROM MRSTY";
	private static final Logger logger = LoggerFactory.getLogger(ConceptToSchemeRlspWriter.class);

	@Required
	public void setBulkImportService(BulkImportService bulkImportService) {
		this.bulkImportService = bulkImportService;
	}

	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Required
	public void setUmlsCacheService(UmlsCacheService umlsCacheService) {
		this.umlsCacheService = umlsCacheService;
	}

	public void init() throws SQLException {
		// streaming connection to MYSQL see:
		connection = dataSource.getConnection();
		statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		statement.setFetchSize(Integer.MIN_VALUE);
	}

	public void writeAll() throws SQLException {
		ResultSet rs = statement.executeQuery(GET_CONCEPT_TO_SCHEME_RELATIONS_SQL);
		int ctr = 0;
		try {
			while (rs.next()) {
				String cui = rs.getString("CUI");
				String sty = rs.getString("STY");
				String subjectValue = umlsCacheService.getValue(cui);
				String objectValue = umlsCacheService.getValue(sty);
				if (subjectValue != null && objectValue != null) {
					ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(subjectValue,
							objectValue, null, SemanticRelationshipCategory.IN_SCHEME,
							ConceptRelationshipCategory.AUTHORITATIVE, 1);
					bulkImportService.validateAndCreateRelationship(conceptRelationshipImpl);
					if (++ctr % UmlsUtils.BATCH_SIZE == 0) {
						logger.info("inserted concept-scheme rlsp: {}", ctr);
					}
				} else {
					logger.error("subject:{} object:{} for cui:{} sty:{}", new Object[] { subjectValue, objectValue,
							cui, sty });
				}
			}
		} finally {
			rs.close();
		}
	}

	public void destroy() throws SQLException {
		statement.close();
		connection.close();
	}

}
