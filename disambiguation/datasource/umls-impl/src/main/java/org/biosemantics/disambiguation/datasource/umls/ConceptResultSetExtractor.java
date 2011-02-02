package org.biosemantics.disambiguation.datasource.umls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ConceptResultSetExtractor implements ResultSetExtractor<Concept> {

	private static final String NOCODE = "NOCODE";
	private static final String SCR = "scr";
	private String defaultDomain;

	@Required
	public void setDefaultDomain(String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	@Override
	public Concept extractData(ResultSet resultSet) throws SQLException, DataAccessException {
		ConceptImpl conceptImpl = new ConceptImpl();
		String cui = null;
		while (resultSet.next()) {
			cui = resultSet.getString("CUI");
			// String sui = resultSet.getString("SUI");
			String ts = resultSet.getString("TS");
			String isPref = resultSet.getString("ISPREF");
			String stt = resultSet.getString("STT");// TS, ISPREF, STT
			String lat = resultSet.getString("LAT");
			String str = resultSet.getString("STR");
			String sab = resultSet.getString("SAB");
			String code = resultSet.getString("CODE");
			LabelImpl labelImpl = new LabelImpl(str, getLanguage(lat));
			// label
			if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
				conceptImpl.addLabelByType(LabelType.PREFERRED, labelImpl);
			} else {
				conceptImpl.addLabelByType(LabelType.ALTERNATE, labelImpl);
			}
			// notation
			if (!code.equals(NOCODE)) {
				conceptImpl.addNotations(new NotationImpl(getDomainUuidForSab(), sab));
			}
			conceptImpl.addNotations(new NotationImpl(defaultDomain, cui));
		}
		return conceptImpl;
	}

	private String getDomainUuidForSab() {
		// TODO Auto-generated method stub
		return null;
	}

	private Language getLanguage(String lat) {
		lat = lat.toLowerCase();
		// http://www.loc.gov/standards/iso639-2/php/code_changes.php SCR has
		// been deprecated
		if (lat.equalsIgnoreCase(SCR)) {
			lat = LanguageImpl.HR.getIso6392Code();
		}
		Language language = LanguageUtility.getLanguageByIso6392Code(lat);
		return language;
	}

}
