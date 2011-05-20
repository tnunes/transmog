package org.biosemantics.datasource.umls.concept;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.datasource.umls.cache.KeyValue;
import org.biosemantics.datasource.umls.cache.UmlsCacheService;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.biosemantics.disambiguation.domain.impl.ConceptLabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DomainWriter {

	private BulkImportService bulkImportService;
	private UmlsCacheService umlsCacheService;
	private DataSource dataSource;
	private Connection connection;
	private Statement statement;

	private static final String GET_ALL_DOMAINS_SQL = "SELECT RSAB, RCUI, VCUI, SON from MRSAB";
	private static final Logger logger = LoggerFactory.getLogger(DomainWriter.class);

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
		ResultSet rs = statement.executeQuery(GET_ALL_DOMAINS_SQL);
		int ctr = 0;
		try {
			while (rs.next()) {
				List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
				String son = rs.getString("SON");
				String rsab = rs.getString("RSAB");
				Label sonLabel = new LabelImpl(LanguageImpl.EN, son);
				long sonLabelId = bulkImportService.createLabel(sonLabel);
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, String.valueOf(sonLabelId)),
						LabelType.PREFERRED));

				Label rsabLabel = new LabelImpl(LanguageImpl.EN, rsab);
				long rsabLabelId = bulkImportService.createLabel(rsabLabel);
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, String.valueOf(rsabLabelId)),
						LabelType.ALTERNATE));
				//umlsCacheService.add(new KeyValue(rsab, String.valueOf(rsabLabelId)));
				// FIXME: get other labels as well from RCUI and VCUI
				StringBuilder fullText = new StringBuilder(son).append(UmlsUtils.SEPERATOR).append(rsab);
				long id = bulkImportService.createUmlsConcept(ConceptType.DOMAIN, conceptLabels, null, fullText.toString());
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
