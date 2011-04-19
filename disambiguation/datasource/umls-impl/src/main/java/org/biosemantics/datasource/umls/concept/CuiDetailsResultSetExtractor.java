package org.biosemantics.datasource.umls.concept;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptLabelImpl;
import org.biosemantics.conceptstore.utils.domain.impl.NotationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class CuiDetailsResultSetExtractor implements ResultSetExtractor<Concept> {

	private static final String NOCODE = "NOCODE";
	private Logger logger = LoggerFactory.getLogger(CuiDetailsResultSetExtractor.class);

	@Override
	public Concept extractData(ResultSet rs) throws SQLException, DataAccessException {
		ConceptImpl conceptImpl = new ConceptImpl();
		String cui = null;
		while (rs.next()) {
			cui = rs.getString("CUI");
			String ts = rs.getString("TS");
			String isPref = rs.getString("ISPREF");
			String stt = rs.getString("STT");// TS, ISPREF, STT
			String lat = rs.getString("LAT");
			String str = rs.getString("STR");
			String sab = rs.getString("SAB");
			String code = rs.getString("CODE");
			ConceptLabelImpl conceptLabelImpl = new ConceptLabelImpl(UmlsUtils.getLanguage(lat), str,
					UmlsUtils.getLabelType(ts, isPref, stt));
			conceptImpl.addConceptLabels(conceptLabelImpl);
			// notation can be NOCODE, ignore!
			if (!code.equals(NOCODE)) {
				NotationImpl notationImpl = new NotationImpl(sab, code);
				conceptImpl.addNotations(notationImpl);
			}
		}
		// cui as notation
		if (cui == null) {
			logger.error("cui is null");
		} else {
			NotationImpl notationImpl = new NotationImpl(UmlsUtils.DEFAULT_SAB, cui);
			conceptImpl.addNotations(notationImpl);
		}
		return conceptImpl;
	}
}
