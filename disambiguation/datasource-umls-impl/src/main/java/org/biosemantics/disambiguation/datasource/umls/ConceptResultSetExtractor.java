package org.biosemantics.disambiguation.datasource.umls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.disambiguation.datasource.umls.DomainIterator.UmlsDomain;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConceptResultSetExtractor implements ResultSetExtractor<ConceptDetail> {

	private static final String NOCODE = "NOCODE";
	private static final String SCR = "scr";
	//private static final Logger logger = LoggerFactory.getLogger(ConceptResultSetExtractor.class);

	@Override
	public ConceptDetail extractData(ResultSet resultSet) throws SQLException, DataAccessException {
		ConceptDetail conceptDetail = new ConceptDetail();
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
			// add unique strings as labels for a concept
			if (!labels.containsKey(sui + lat)) {
				// to avoid duplicates
				labels.put(sui + lat, null);
				if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
					conceptDetail.addLabel(new LabelDetail(str, getLanguage(lat), LabelType.PREFERRED));
				} else {
					conceptDetail.addLabel(new LabelDetail(str, getLanguage(lat), LabelType.ALTERNATE));
				}
			}
			// notation
			// FIXME HACK use proper keys here
			if (!notations.containsKey(code + sab) && !code.equals(NOCODE)) {
				// to avoid duplicates
				notations.put(code + sab, null);
				conceptDetail.addNotation(new NotationDetail(code, getCorrectedSab(sab)));
			}
		}
		conceptDetail.addNotation(new NotationDetail(cui, UmlsDomain.getDefaultDomain().name()));
		return conceptDetail;
	}

	private Language getLanguage(String lat) {
		lat = lat.toLowerCase();
		// http://www.loc.gov/standards/iso639-2/php/code_changes.php SCR has
		// been deprecated
		if (lat.equalsIgnoreCase(SCR)) {
			lat = Language.HR.getIso6392Code();
		}
		Language language = DataSourceCommonUtility.getLanguageByIso6392Code(lat);
		return language;
	}

	private String getCorrectedSab(String sab) {
		// we replaced all - and . to _ in domains as they are not supported as domain names
		// FIXME : we need to store the original information
		String correctedSab = sab.replace("-", "_");
		correctedSab = correctedSab.replace(".", "_");
		return correctedSab;
	}

}
