package org.biosemantics.datasource.umls.concept;

import java.sql.Connection;
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
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.datasource.umls.cache.KeyValue;
import org.biosemantics.datasource.umls.cache.UmlsCacheService;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.biosemantics.disambiguation.domain.impl.ConceptLabelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ConceptWriter {

	private DataSource dataSource;
	private BulkImportService bulkImportService;
	private Connection connection;
	private Statement statement;
	private UmlsCacheService umlsCacheService;

	// constants
	private static final String GET_ALL_CUI_SQL = "select CUI, SUI, TS, ISPREF, STT, LAT, STR, SAB, CODE from MRCONSO ORDER BY CUI";
	private static final Logger logger = LoggerFactory.getLogger(ConceptWriter.class);

	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Required
	public void setBulkImportService(BulkImportService bulkImportService) {
		this.bulkImportService = bulkImportService;
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

		ResultSet rs = statement.executeQuery(GET_ALL_CUI_SQL);
		try {
			String previousCui = null;
			Set<String> fullText = new HashSet<String>();
			List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
			List<Long> notations = new ArrayList<Long>();
			int conceptInsertCounter = 0;
			while (rs.next()) {
				String cui = rs.getString("CUI");
				String ts = rs.getString("TS");
				String isPref = rs.getString("ISPREF");
				String stt = rs.getString("STT");// TS, ISPREF, STT
				String lat = rs.getString("LAT");
				String str = rs.getString("STR");
				String sab = rs.getString("SAB");
				String code = rs.getString("CODE");
				String sui = rs.getString("SUI");
				if (previousCui != null && !cui.equals(previousCui)) {
					NotationImpl notationImpl = new NotationImpl(umlsCacheService.getValue(UmlsUtils.DEFAULT_SAB),
							previousCui);
					long cuiNotationNodeId = bulkImportService.createNotation(notationImpl);
					notations.add(cuiNotationNodeId);
					fullText.add(previousCui);
					long uuid = bulkImportService.createUmlsConcept(ConceptType.CONCEPT, conceptLabels, notations,
							UmlsUtils.setToString(fullText));
					fullText.clear();
					conceptLabels.clear();
					notations.clear();
					// add cui to cache
					umlsCacheService.add(new KeyValue(previousCui, String.valueOf(uuid)));
					if (++conceptInsertCounter % UmlsUtils.BATCH_SIZE == 0) {
						logger.info("inserted concepts: {}", conceptInsertCounter);
					}
				}
				// labels
				String value = umlsCacheService.getValue(sui);
				long labelNodeId = 0;
				if (value == null) {
					Label label = new LabelImpl(UmlsUtils.getLanguage(lat), str);
					labelNodeId = bulkImportService.createLabel(label);
					umlsCacheService.add(new KeyValue(sui, String.valueOf(labelNodeId)));
				} else {
					labelNodeId = Long.valueOf(value);
				}
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, String.valueOf(labelNodeId)), UmlsUtils
						.getLabelType(ts, isPref, stt)));
				fullText.add(str);
				// notations
				if (!code.equals(UmlsUtils.NOCODE)) {
					long notationNodeId = 0;
					String notationValue = umlsCacheService.getValue(sab + code);
					if (notationValue == null) {
						String domainUuid = umlsCacheService.getValue(sab);
						if (domainUuid == null) {
							throw new IllegalStateException("domainUUid not found for sab = " + sab);
						}
						NotationImpl notationImpl = new NotationImpl(domainUuid, code);
						notationNodeId = bulkImportService.createNotation(notationImpl);
						umlsCacheService.add(new KeyValue(sab+code, String.valueOf(notationNodeId)));
					} else {
						notationNodeId = Long.valueOf(notationValue);
					}
					notations.add(notationNodeId);
					fullText.add(code);
				}
				// counter
				previousCui = cui;
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
