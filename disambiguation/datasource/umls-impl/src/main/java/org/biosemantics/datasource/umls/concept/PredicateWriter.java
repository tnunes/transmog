package org.biosemantics.datasource.umls.concept;

import java.io.FileReader;
import java.io.IOException;
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

import au.com.bytecode.opencsv.CSVReader;

public class PredicateWriter {

	private BulkImportService bulkImportService;
	private UmlsCacheService umlsCacheService;
	private DataSource dataSource;
	private Connection connection;
	private Statement statement;
	private String predicatesTsvFile;

	private static final String GET_CONCEPT_SCHEME_PREDICATES_SQL = "select distinct RL as RL from SRSTRE2";
	private static final Logger logger = LoggerFactory.getLogger(PredicateWriter.class);//NOPMD

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

	@Required
	public final void setPredicatesTsvFile(String predicatesTsvFile) {
		this.predicatesTsvFile = predicatesTsvFile;
	}

	public void init() throws SQLException {
		// streaming connection to MYSQL see:
		connection = dataSource.getConnection();
		statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		statement.setFetchSize(Integer.MIN_VALUE);
	}

	public String getPredicatesTsvFile() {
		return predicatesTsvFile;
	}

	public void writeAll() throws IOException, SQLException {
		Set<String> predicates = new HashSet<String>();
		CSVReader csvReader = new CSVReader(new FileReader(predicatesTsvFile), '\t');
		List<String[]> allLines = csvReader.readAll();
		for (String[] line : allLines) {
			String column = line[0].trim();
			predicates.add(column);
		}
		ResultSet rs = statement.executeQuery(GET_CONCEPT_SCHEME_PREDICATES_SQL);
		try {
			while (rs.next()) {
				String rl = rs.getString("RL");
				predicates.add(rl);
			}
		} finally {
			rs.close();
		}

		for (String predicateString : predicates) {
			List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
			String value = umlsCacheService.getValue(predicateString);
			if (value == null) {
				long prefLabelNodeId = bulkImportService.createLabel(new LabelImpl(Language.EN, predicateString));
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, String.valueOf(prefLabelNodeId)),
						LabelType.PREFERRED));

				String updatedPredicateString = predicateString.replace('_', ' ');
				long altLabelNodeId = bulkImportService.createLabel(new LabelImpl(Language.EN,
						updatedPredicateString));
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(null, String.valueOf(altLabelNodeId)),
						LabelType.ALTERNATE));
				StringBuilder fullText = new StringBuilder(predicateString);
				long conceptNodeId = bulkImportService.createUmlsConcept(ConceptType.PREDICATE, conceptLabels, null,
						fullText.toString());
				umlsCacheService.add(new KeyValue(predicateString, String.valueOf(conceptNodeId)));
			}
		}

		logger.info("{} predicates created", predicates.size());

	}

	public void destroy() throws SQLException {
		statement.close();
		connection.close();
	}
}
