package org.biosemantics.disambiguation.datasource.umls;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.conceptstore.common.domain.Concept;
import org.biosemantics.conceptstore.common.domain.Label.LabelType;
import org.biosemantics.conceptstore.utils.domain.impl.ConceptImpl;
import org.biosemantics.conceptstore.utils.domain.impl.LabelImpl;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DomainResultSetExtractor implements ResultSetExtractor<Collection<Concept>> {

	
	private static final Logger logger = LoggerFactory.getLogger(DomainResultSetExtractor.class);
	
	private Collection<Concept> concepts = new ArrayList<Concept>();
	private Collection<String> domainCuis = new HashSet<String>();
	@Override
	public Collection<Concept> extractData(ResultSet rs) throws SQLException, DataAccessException {
		while (rs.next()) {
			String preferredLabel = rs.getString("");
			ConceptImpl conceptImpl = new ConceptImpl();
			conceptImpl.addLabelByType(LabelType.PREFERRED, new LabelImpl(preferredLabel, LanguageImpl.EN));
			concepts.add(conceptImpl);
			String domainCui =  rs.getString("");
			String domainCui2 = rs.getString("");
			//add to domainCui
		}
		
		return concepts;
	}

}
