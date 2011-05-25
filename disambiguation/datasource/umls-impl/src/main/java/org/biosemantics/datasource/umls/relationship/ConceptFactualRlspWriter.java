package org.biosemantics.datasource.umls.relationship;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipCategory;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptRelationshipImpl;
import org.biosemantics.datasource.umls.cache.KeyValue;
import org.biosemantics.datasource.umls.cache.UmlsCacheService;
import org.biosemantics.datasource.umls.concept.UmlsUtils;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ConceptFactualRlspWriter {
	private BulkImportService bulkImportService;
	private UmlsCacheService umlsCacheService;
	private DataSource dataSource;
	private Connection connection;
	private Statement statement;
	private static final String GET_ALL_FACTUAL_RLSP_SQL = "select CUI1, CUI2, REL from MRREL where CUI1 != CUI2";
	private static final Logger logger = LoggerFactory.getLogger(ConceptFactualRlspWriter.class);

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
		ResultSet rs = statement.executeQuery(GET_ALL_FACTUAL_RLSP_SQL);
		int ctr = 0;
		try {
			while (rs.next()) {
				String cui1 = rs.getString("CUI1");
				String cui2 = rs.getString("CUI2");
				String rel = rs.getString("REL");
				// factual relationships are stored in the form cui2->rel->cui1 hence inverting subject object here
				String subjectValue = umlsCacheService.getValue(cui1);
				String objectValue = umlsCacheService.getValue(cui2);
				SemanticRelationshipCategory semanticRelationshipCategory = UmlsUtils.getConceptRelationshipType(rel);
				if (subjectValue != null && objectValue != null) {
					if (!checkExists(subjectValue, objectValue, semanticRelationshipCategory)) {
						ConceptRelationshipImpl conceptRelationshipImpl = new ConceptRelationshipImpl(subjectValue,
								objectValue, null, semanticRelationshipCategory,
								ConceptRelationshipCategory.AUTHORITATIVE, 1);
						bulkImportService.createRelationship(conceptRelationshipImpl);
						// add to cache
						umlsCacheService.add(new KeyValue(subjectValue + objectValue
								+ semanticRelationshipCategory.name(), subjectValue));
						if (++ctr % UmlsUtils.BATCH_SIZE == 0) {
							logger.info("inserted concept-concept rlsp: {}", ctr);
						}
					}
				} else {
					logger.error("subject:{} object:{} for cui1:{} cui2:{}", new Object[] { subjectValue, objectValue,
							cui1, cui2 });
				}
			}
		} finally {
			rs.close();
		}
	}

	private boolean checkExists(String subjectValue, String objectValue,
			SemanticRelationshipCategory semanticRelationshipCategory) {
		switch (semanticRelationshipCategory) {
		case RELATED:
			if (umlsCacheService.getValue(subjectValue + objectValue + semanticRelationshipCategory.name()) == null) {
				if (umlsCacheService.getValue(objectValue + subjectValue + semanticRelationshipCategory.name()) == null) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		case HAS_BROADER_CONCEPT:
			if (umlsCacheService.getValue(subjectValue + objectValue
					+ SemanticRelationshipCategory.HAS_BROADER_CONCEPT.name()) == null) {
				if (umlsCacheService.getValue(objectValue + subjectValue
						+ SemanticRelationshipCategory.HAS_NARROWER_CONCEPT.name()) == null) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		case HAS_NARROWER_CONCEPT:
			if (umlsCacheService.getValue(subjectValue + objectValue
					+ SemanticRelationshipCategory.HAS_NARROWER_CONCEPT.name()) == null) {
				if (umlsCacheService.getValue(objectValue + subjectValue
						+ SemanticRelationshipCategory.HAS_BROADER_CONCEPT.name()) == null) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		default:
			throw new IllegalStateException("semantic relation not found");
		}

	}

	public void destroy() throws SQLException {
		statement.close();
		connection.close();
	}

}
