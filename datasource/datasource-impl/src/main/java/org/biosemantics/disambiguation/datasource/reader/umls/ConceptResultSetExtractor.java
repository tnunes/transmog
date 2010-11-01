package org.biosemantics.disambiguation.datasource.reader.umls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.datasource.reader.DataSourceCommonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConceptResultSetExtractor implements ResultSetExtractor<Concept> {

	private static final String NOCODE = "NOCODE";
	private Concept defaultDomain;
	private DomainIterator domainIterator;
	private static final String SCR = "scr";
	private static final Logger logger = LoggerFactory.getLogger(ConceptResultSetExtractor.class);

	public ConceptResultSetExtractor(Concept defaultDomain, DomainIterator domainIterator) {
		this.defaultDomain = defaultDomain;
		this.domainIterator = domainIterator;
	}

	@Override
	public Concept extractData(ResultSet resultSet) throws SQLException, DataAccessException {
		ConceptImpl concept = new ConceptImpl();
		String cui = null;
		Map<String, Object> labels = new HashMap<String, Object>();
		Map<String, Object> notations = new HashMap<String, Object>();
		while (resultSet.next()) {
			cui = resultSet.getString("CUI");
			String sui = resultSet.getString("SUI");
			String ts = resultSet.getString("TS");
			String isPref = resultSet.getString("ISPREF");
			String stt = resultSet.getString("STT");// TS, ISPREF, STT
			String lat = resultSet.getString("LAT");
			String str = resultSet.getString("STR");
			String sab = resultSet.getString("SAB");
			String code = resultSet.getString("CODE");
			// add unique strings as labels
			if (!labels.containsKey(sui)) {
				// label
				Language labelLanguage = getLanguage(lat);
				LabelImpl labelImpl = new LabelImpl(str, labelLanguage);
				labels.put(sui, null);
				if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
					concept.addLabelByType(LabelType.PREFERRED, labelImpl);
				} else {
					concept.addLabelByType(LabelType.ALTERNATE, labelImpl);
				}
			}
			// notation
			// FIXME HACK use proper keys here
			if (!notations.containsKey(code + sab) && !code.equals(NOCODE)) {
				Concept domain = getDomain(sab);
				NotationImpl notationImpl = new NotationImpl(domain, code);
				concept.addNotations(notationImpl);
				notations.put(code + sab, null);
			}
		}
		NotationImpl notationImpl = new NotationImpl(defaultDomain, cui);
		concept.addNotations(notationImpl);
		return concept;
	}

	private Language getLanguage(String lat) {
		lat = lat.toLowerCase();
		// http://www.loc.gov/standards/iso639-2/php/code_changes.php SCR has
		// been deprecated
		if (lat.equalsIgnoreCase(SCR)) {
			lat = Language.HR.getIso6392Code();
		}
		logger.debug(lat);
		return DataSourceCommonUtility.getLanguageByIso6392Code(lat);
	}

	private Concept getDomain(String sab) {
		sab = sab.replace("-", "_");
		sab = sab.replace(".", "_");
		Concept domain = domainIterator.getDomainByNotationCode(sab);
		if (domain == null) {
			logger.warn("no domain found for string {}", sab);
		}
		return domain;
	}

}
