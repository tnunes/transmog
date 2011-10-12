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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
	private PreparedStatement conceptLabelPreparedStatement;
	// private static final String GET_ALL_DOMAINS_SQL = "SELECT RSAB, RCUI, VCUI, SON from MRSAB";
	private static final String GET_ALL_DISTINCT_DOMAINS_SQL = "SELECT DISTINCT(VSAB) from MRSAB";
	private static final String GET_ALL_DOMAIN_LABELS_SQL = "SELECT SON, RCUI, VCUI from MRSAB where VSAB = ?";
	// private static final String GET_UMLS_DOMAIN =
	// "SELECT SON, VSAB, RSAB from MRSAB where SON = \"UMLS Metathesaurus\" ";
	private static final String GET_CONCEPT_LABELS = "select CUI, TS, ISPREF, STT, LAT, STR from MRCONSO where CUI = ? ";
	private static final Logger logger = LoggerFactory.getLogger(DomainWriter.class);// NOPMD
	private Set<String> domainCuis = new HashSet<String>();

	public Set<String> getDomainCuis() {
		return domainCuis;
	}

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
		conceptLabelPreparedStatement = connection.prepareStatement(GET_CONCEPT_LABELS);
	}

	public void writeAll() throws SQLException {
		ResultSet rs = statement.executeQuery(GET_ALL_DISTINCT_DOMAINS_SQL);
		int ctr = 0;
		Set<String> fullText = new HashSet<String>();
		try {
			while (rs.next()) {
				List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
				String vsab = rs.getString("VSAB");
				Label rsabLabel = new LabelImpl(Language.EN, vsab);
				long rsabLabelId = bulkImportService.createLabel(rsabLabel);
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, String.valueOf(rsabLabelId)),
						LabelType.HIDDEN));
				fullText.add(vsab);
				// get all SON for this RSAB
				preparedStatement.setString(1, vsab);
				ResultSet labelResultSet = preparedStatement.executeQuery();
				String son = null;
				String rcui = null;
				String vcui = null;
				while (labelResultSet.next()) {
					son = labelResultSet.getString("SON");
					rcui = labelResultSet.getString("RCUI");
					vcui = labelResultSet.getString("VCUI");
					Set<String> cuis = new HashSet<String>();
					if (!StringUtils.isEmpty(rcui)) {
						cuis.add(rcui);
					}
					if (!StringUtils.isEmpty(vcui)) {
						cuis.add(vcui);
					}
					if (!CollectionUtils.isEmpty(cuis)) {
						List<ConceptLabel> cuiConceptLabels = getConceptLabels(cuis);
						conceptLabels.addAll(cuiConceptLabels);
						domainCuis.addAll(cuis);
					}
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
				umlsCacheService.add(new KeyValue(vsab, String.valueOf(id)));
				if (!StringUtils.isEmpty(rcui)) {
					umlsCacheService.add(new KeyValue(rcui, String.valueOf(id)));
				}
				if (!StringUtils.isEmpty(vcui)) {
					umlsCacheService.add(new KeyValue(vcui, String.valueOf(id)));
				}
				ctr++;
			}
			logger.info("{} domains created", ctr);
		} finally {
			rs.close();
		}
	}

	private List<ConceptLabel> getConceptLabels(Set<String> cuis) throws SQLException {
		List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
		for (String cui : cuis) {
			conceptLabelPreparedStatement.setString(1, cui);
			ResultSet labelResultSet = conceptLabelPreparedStatement.executeQuery();
			while (labelResultSet.next()) {
				String str = labelResultSet.getString("STR");
				String lat = labelResultSet.getString("LAT");
				String ts = labelResultSet.getString("TS");
				String isPref = labelResultSet.getString("ISPREF");
				String stt = labelResultSet.getString("STT");
				String value = umlsCacheService.getValue(str);
				if (value == null) {
					value = String
							.valueOf(bulkImportService.createLabel(new LabelImpl(UmlsUtils.getLanguage(lat), str)));
					umlsCacheService.add(new KeyValue(str, value));
				}
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, value), UmlsUtils.getLabelType(ts, isPref,
						stt)));
			}
		}
		return conceptLabels;
	}

	public void writeAll1() throws SQLException {
		ResultSet rs = statement.executeQuery(GET_ALL_DISTINCT_DOMAINS_SQL);
		int ctr = 0;
		Set<String> fullText = new HashSet<String>();
		try {
			while (rs.next()) {
				List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
				String vsab = rs.getString("VSAB");
				Label rsabLabel = new LabelImpl(Language.EN, vsab);
				long vsabLabel = bulkImportService.createLabel(rsabLabel);
				conceptLabels
						.add(new ConceptLabelImpl(new LabelImpl(null, String.valueOf(vsabLabel)), LabelType.HIDDEN));
				fullText.add(vsab);
				// get all SON for this RSAB
				preparedStatement.setString(1, vsab);
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
				umlsCacheService.add(new KeyValue(vsab, String.valueOf(id)));
				ctr++;
			}
			logger.info("{} domains created", ctr);
		} finally {
			rs.close();
		}
	}

	public void destroy() throws SQLException {
		statement.close();
		preparedStatement.close();
		conceptLabelPreparedStatement.close();
		connection.close();
	}
}
