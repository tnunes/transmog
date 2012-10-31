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
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.ConceptLabel;
import org.biosemantics.conceptstore.common.domain.ConceptType;
import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
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
	private PreparedStatement conceptPreparedStatement;
	// private static final String GET_ALL_DOMAINS_SQL = "SELECT RSAB, RCUI, VCUI, SON from MRSAB";
	private static final String GET_ALL_DISTINCT_DOMAINS_SQL = "SELECT DISTINCT(VSAB) from MRSAB";
	private static final String GET_ALL_DOMAIN_LABELS_SQL = "SELECT SON, SSN, RCUI, VCUI from MRSAB where VSAB = ?";
	// private static final String GET_UMLS_DOMAIN =
	// "SELECT SON, VSAB, RSAB from MRSAB where SON = \"UMLS Metathesaurus\" ";
	private static final String GET_CONCEPT_LABELS = "select CUI, SUI, TS, ISPREF, STT, LAT, STR, SAB, CODE from MRCONSO where CUI = ? ";
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
		conceptPreparedStatement = connection.prepareStatement(GET_CONCEPT_LABELS);
	}

	public void writeAll() throws SQLException {
		List<String> vsabs = new ArrayList<String>();
		ResultSet vsabResultSet = statement.executeQuery(GET_ALL_DISTINCT_DOMAINS_SQL);
		try {
			while (vsabResultSet.next()) {
				String vsab = vsabResultSet.getString("VSAB");
				vsabs.add(vsab);
				long nodeId = bulkImportService.createUmlsConcept(ConceptType.DOMAIN, null, null, null);
				umlsCacheService.addDomainNode(new KeyValue(vsab, String.valueOf(nodeId)));
			}
		} finally {
			vsabResultSet.close();
		}
		// add labels to
		for (String vsab : vsabs) {
			preparedStatement.setString(1, vsab);
			ResultSet labelResultSet = preparedStatement.executeQuery();
			Set<String> fullTextSet = new HashSet<String>();
			List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
			List<Long> notations = new ArrayList<Long>();
			try {
				while (labelResultSet.next()) {

					String son = labelResultSet.getString("SON");
					if (!StringUtils.isEmpty(son)) {
						fullTextSet.add(son);
						String value = umlsCacheService.getValue(son);
						if (value == null) {
							value = String.valueOf(bulkImportService.createLabel(new LabelImpl(Language.EN, son)));
							umlsCacheService.add(new KeyValue(Language.EN.name() + son, value));
						}
						conceptLabels.add(new ConceptLabelImpl(new LabelImpl(Language.EN, value), LabelType.ALTERNATE));
					}
					String ssn = labelResultSet.getString("SSN");
					if (!StringUtils.isEmpty(ssn)) {
						fullTextSet.add(ssn);
						String value = umlsCacheService.getValue(ssn);
						if (value == null) {
							value = String.valueOf(bulkImportService.createLabel(new LabelImpl(Language.EN, ssn)));
							umlsCacheService.add(new KeyValue(Language.EN.name() + ssn, value));
						}
						conceptLabels.add(new ConceptLabelImpl(new LabelImpl(Language.EN, value), LabelType.PREFERRED));
					}
					String rcui = labelResultSet.getString("RCUI");
					if (!StringUtils.isEmpty(rcui)) {
						Concept concept = getConcept(rcui, fullTextSet);
						conceptLabels.addAll(concept.getLabels());
						for (Notation not : concept.getNotations()) {
							notations.add(Long.valueOf(not.getCode()));
						}
					}

					String vcui = labelResultSet.getString("VCUI");
					if (!StringUtils.isEmpty(vcui)) {
						Concept concept = getConcept(vcui, fullTextSet);
						conceptLabels.addAll(concept.getLabels());
						for (Notation not : concept.getNotations()) {
							notations.add(Long.valueOf(not.getCode()));
						}
					}

				}
			} finally {
				labelResultSet.close();
			}
			bulkImportService.updateUmlsConcept(Long.valueOf(umlsCacheService.getDomainNode(vsab)), ConceptType.DOMAIN,
					conceptLabels, notations, UmlsUtils.setToString(fullTextSet));
		}

	}

	private Concept getConcept(String cui, Set<String> fullTextSet) throws SQLException {
		ConceptImpl conceptImpl = new ConceptImpl();
		conceptPreparedStatement.setString(1, cui);
		fullTextSet.add(cui);
		ResultSet conceptResultSet = conceptPreparedStatement.executeQuery();
		List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
		List<Notation> notations = new ArrayList<Notation>();
		try {
			while (conceptResultSet.next()) {
				String ts = conceptResultSet.getString("TS");
				String isPref = conceptResultSet.getString("ISPREF");
				String stt = conceptResultSet.getString("STT");// TS, ISPREF, STT
				String lat = conceptResultSet.getString("LAT");
				String str = conceptResultSet.getString("STR");
				String sab = conceptResultSet.getString("SAB");
				String code = conceptResultSet.getString("CODE");
				fullTextSet.add(str);
				Language language = UmlsUtils.getLanguage(lat);
				String value = umlsCacheService.getValue(language.name() + str);
				if (value == null) {
					value = String.valueOf(bulkImportService.createLabel(new LabelImpl(language, str)));
					umlsCacheService.add(new KeyValue(language.name() + str, value));
				}
				LabelType labelType = UmlsUtils.getLabelType(ts, isPref, stt);
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(language, value), labelType));

				if (!code.equals(UmlsUtils.NOCODE)) {
					fullTextSet.add(code);
					value = umlsCacheService.getValue(sab + code);
					if (value == null) {
						String domainNodeId = umlsCacheService.getDomainNode(sab);
						if (domainNodeId == null) {
							logger.error("could not retrieve domain node id for SAB {}", sab);
							throw new IllegalStateException();
						}
						value = String.valueOf(bulkImportService.createNotation(new NotationImpl(domainNodeId, code)));
						umlsCacheService.add(new KeyValue(language.name() + str, value));
					}
					notations.add(new NotationImpl(null, value));
				}
			}
		} finally {
			conceptResultSet.close();
		}
		conceptImpl.addConceptLabels(conceptLabels);
		conceptImpl.addNotations(notations);
		return conceptImpl;
	}

	public void destroy() throws SQLException {
		statement.close();
		preparedStatement.close();
		conceptPreparedStatement.close();
		connection.close();
	}

	private List<ConceptLabel> getConceptLabels(Set<String> cuis) throws SQLException {
		List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
		for (String cui : cuis) {
			conceptPreparedStatement.setString(1, cui);
			ResultSet labelResultSet = conceptPreparedStatement.executeQuery();
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
}
