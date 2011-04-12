package org.biosemantics.datasource.umls.concept;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptLabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DomainResultSetExtractor implements ResultSetExtractor<Collection<Concept>> {

	@Override
	public Collection<Concept> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Collection<Concept> domains = new HashSet<Concept>();
		while (rs.next()) {
			String son = rs.getString("SON");
			String rsab = rs.getString("RSAB");
			ConceptImpl domain = new ConceptImpl();
			domain.addConceptLabels(new ConceptLabelImpl(LanguageImpl.EN, son, LabelType.PREFERRED));
			domain.addConceptLabels(new ConceptLabelImpl(LanguageImpl.EN, rsab, LabelType.ALTERNATE));
			domains.add(domain);
		}
		return domains;
	}

}
