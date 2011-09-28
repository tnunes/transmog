package org.biosemantics.datasource.umls.relationship;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipSource;
import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.datasource.umls.cache.UmlsCacheService;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class SchemeRlspWriter {

	private BulkImportService bulkImportService;
	private UmlsCacheService umlsCacheService;
	private DataSource dataSource;
	private Connection connection;
	private Statement statement;
	// avoiding self linking records: as self rlsps are not supported by neo4j
	private static final String GET_CONCEPT_SCHEME_RELATIONS_SQL = "select STY1, RL, STY2 from SRSTRE2  where STY1 != STY2";
	private static final Logger logger = LoggerFactory.getLogger(SchemeRlspWriter.class);//NOPMD

	@Required
	public final void setBulkImportService(BulkImportService bulkImportService) {
		this.bulkImportService = bulkImportService;
	}

	@Required
	public final void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Required
	public final void setUmlsCacheService(UmlsCacheService umlsCacheService) {
		this.umlsCacheService = umlsCacheService;
	}

	public void init() throws SQLException {
		// streaming connection to MYSQL see:
		connection = dataSource.getConnection();
		statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		statement.setFetchSize(Integer.MIN_VALUE);
	}

	public void writeAll() throws SQLException {
		ResultSet rs = statement.executeQuery(GET_CONCEPT_SCHEME_RELATIONS_SQL);
		int ctr = 0;
		try {
			while (rs.next()) {
				String sty1 = rs.getString("STY1");
				String rl = rs.getString("RL");
				String sty2 = rs.getString("STY2");
				// get fromNode
				String subjectValue = umlsCacheService.getValue(sty1);
				String predicateValue = umlsCacheService.getValue(rl);
				String objectValue = umlsCacheService.getValue(sty2);
				if (subjectValue != null && predicateValue != null && objectValue != null) {
					ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(subjectValue,
							objectValue, predicateValue, ConceptRelationshipType.RELATED,
							ConceptRelationshipSource.AUTHORITATIVE, Integer.MAX_VALUE);
					bulkImportService.validateAndCreateRelationship(conceptRelationshipImpl);
					ctr++;
				} else {
					logger.error("subject:{} object:{} for sty1:{} sty2:{} rl:{}", new Object[] { subjectValue,
							objectValue, sty1, sty2, rl });
					// throw new IllegalStateException();
				}
			}
			logger.info("{} conceptscheme-conceptscheme rlsp created", ctr);
		} finally {
			rs.close();

		}
	}

	public void destroy() throws SQLException {
		statement.close();
		connection.close();
	}

}
