package org.biosemantics.datasource.umls.concept;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.datasource.umls.cache.KeyValue;
import org.biosemantics.datasource.umls.cache.UmlsCacheService;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.biosemantics.disambiguation.domain.impl.ConceptLabelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DomainWriter {

	private BulkImportService bulkImportService;
	private UmlsCacheService umlsCacheService;
	private DataSource dataSource;
	private Connection connection;
	private Statement statement;
	private PreparedStatement preparedStatement;

	// private static final String GET_ALL_DOMAINS_SQL = "SELECT RSAB, RCUI, VCUI, SON from MRSAB";
	private static final String GET_ALL_DISTINCT_DOMAINS_SQL = "SELECT DISTINCT(RSAB) from MRSAB";
	private static final String GET_ALL_DOMAIN_LABELS_SQL = "SELECT DISTINCT(SON) from MRSAB where RSAB = ?";
	private static final Logger logger = LoggerFactory.getLogger(DomainWriter.class);//NOPMD

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
		statement = connection.createStatement();
		preparedStatement = connection.prepareStatement(GET_ALL_DOMAIN_LABELS_SQL);
	}

	public void writeAll() throws SQLException {
		ResultSet rs = statement.executeQuery(GET_ALL_DISTINCT_DOMAINS_SQL);
		int ctr = 0;
		Set<String> fullText = new HashSet<String>();
		try {
			while (rs.next()) {
				List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
				String rsab = rs.getString("RSAB");
				Label rsabLabel = new LabelImpl(Language.EN, rsab);
				long rsabLabelId = bulkImportService.createLabel(rsabLabel);
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, String.valueOf(rsabLabelId)),
						LabelType.HIDDEN));
				fullText.add(rsab);
				//get all SON for this RSAB
				preparedStatement.setString(1, rsab);
				ResultSet labelResultSet = preparedStatement.executeQuery();
				while (labelResultSet.next()) {
					String son = labelResultSet.getString("SON");
					String sonLabelId = umlsCacheService.getValue(son);
					if (sonLabelId == null) {
						Label sonLabel = new LabelImpl(Language.EN, son);
						fullText.add(son);
						sonLabelId = String.valueOf(bulkImportService.createLabel(sonLabel));
						conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, sonLabelId), LabelType.ALTERNATE));
						umlsCacheService.add(new KeyValue(son, sonLabelId));
					} else {
						conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, sonLabelId), LabelType.ALTERNATE));
					}
				}
				labelResultSet.close();
				
				long id = bulkImportService.createUmlsConcept(ConceptType.DOMAIN, conceptLabels, null,
						UmlsUtils.setToString(fullText));
				umlsCacheService.add(new KeyValue(rsab, String.valueOf(id)));
				ctr++;
			}
			logger.info("{} domains created", ctr);
		} finally {
			rs.close();
		}
	}

	public void destroy() throws SQLException {
		statement.close();
		connection.close();
	}
}
