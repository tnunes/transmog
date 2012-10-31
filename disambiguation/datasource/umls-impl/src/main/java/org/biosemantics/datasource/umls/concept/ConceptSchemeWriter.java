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
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.Notation;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.datasource.umls.cache.KeyValue;
import org.biosemantics.datasource.umls.cache.UmlsCacheService;
import org.biosemantics.disambiguation.bulkimport.service.BulkImportService;
import org.biosemantics.disambiguation.domain.impl.ConceptLabelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ConceptSchemeWriter {

	private BulkImportService bulkImportService;
	private UmlsCacheService umlsCacheService;
	private DataSource dataSource;
	private Connection connection;
	private Statement statement;

	private static final String GET_ALL_CONCEPT_SCHEMES_SQL = "select ABR, STY_RL, UI, DEF from SRDEF where RT='STY'";
	private static final Logger logger = LoggerFactory.getLogger(ConceptSchemeWriter.class);// NOPMD

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
		ResultSet rs = statement.executeQuery(GET_ALL_CONCEPT_SCHEMES_SQL);
		int ctr = 0;
		try {
			while (rs.next()) {
				String styRl = rs.getString("STY_RL");
				String ui = rs.getString("UI");
				String abr = rs.getString("ABR");

				List<ConceptLabel> conceptLabels = new ArrayList<ConceptLabel>();
				List<Long> notations = new ArrayList<Long>();

				String value = umlsCacheService.getValue(Language.EN.name() + styRl);
				if (value == null) {
					value = String.valueOf(bulkImportService.createLabel(new LabelImpl(Language.EN, styRl)));
					umlsCacheService.add(new KeyValue(Language.EN.name() + styRl, value));
				}
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(Language.EN, value), LabelType.PREFERRED));

				value = umlsCacheService.getValue(Language.EN.name() + abr);
				if (value == null) {
					value = String.valueOf(bulkImportService.createLabel(new LabelImpl(Language.EN, abr)));
					umlsCacheService.add(new KeyValue(Language.EN.name() + abr, value));
				}
				conceptLabels.add(new ConceptLabelImpl(new LabelImpl(Language.EN, value), LabelType.ALTERNATE));

				value = umlsCacheService.getValue(UmlsUtils.DEFAULT_SAB + ui);
				if (value == null) {
					Notation notation = new NotationImpl(umlsCacheService.getDomainNode(UmlsUtils.DEFAULT_SAB), ui);
					value = String.valueOf(bulkImportService.createNotation(notation));
					umlsCacheService.add(new KeyValue(UmlsUtils.DEFAULT_SAB + ui, value));
				}

				notations.add(Long.valueOf(value));
				StringBuilder fullText = new StringBuilder(styRl).append(UmlsUtils.SEPERATOR).append(ui)
						.append(UmlsUtils.SEPERATOR).append(abr);
				long conceptNodeId = bulkImportService.createUmlsConcept(ConceptType.CONCEPT_SCHEME, conceptLabels,
						notations, fullText.toString());
				umlsCacheService.add(new KeyValue(styRl, String.valueOf(conceptNodeId)));
				ctr++;
			}
			logger.info("{} concepts schemes created", ctr);

		} finally {
			rs.close();
		}
	}

	public void destroy() throws SQLException {
		statement.close();
		connection.close();
	}

}
