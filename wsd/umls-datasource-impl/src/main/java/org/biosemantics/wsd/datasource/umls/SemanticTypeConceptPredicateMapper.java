package org.biosemantics.wsd.datasource.umls;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import au.com.bytecode.opencsv.*;

@Component
public class SemanticTypeConceptPredicateMapper {

	public void init() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("predicate-mapping.txt");
		if (is == null) {
			logger.info("predicate-mapping.txt file found");
		} else {
			CSVReader csvReader = new CSVReader(new InputStreamReader(is), '\t');
			List<String[]> lines = csvReader.readAll();
			for (String[] columns : lines) {
				String strRelationship = columns[1].trim();
				String from = columns[0].trim();
				String to = columns[4].trim();
				String related = columns[2].trim();
				SemanticTypePredicate semanticTypePredicate = null;
				if (strRelationship.equalsIgnoreCase("subProp")) {
					semanticTypePredicate = new SemanticTypePredicate(from, SemanticTypePredicate.SUB_PROP, related);
				} else if (strRelationship.equalsIgnoreCase("eqProp")) {
					semanticTypePredicate = new SemanticTypePredicate(from, SemanticTypePredicate.EQ_PROP, related);
				}
				if (semanticTypePredicate != null) {
					if (!StringUtils.isBlank(from)) {
						// add original name
						mappingMap.put(from, semanticTypePredicate);
					}
					if (!StringUtils.isBlank(to)) {
						// add inverse name
						mappingMap.put(to, semanticTypePredicate);
					}
				}
			}
			logger.info("{} predicates mapped.", mappingMap.size());
			csvReader.close();
		}
	}

	public SemanticTypePredicate getMappedSemanticTypePredicate(String conceptPredicate) {
		return mappingMap.get(conceptPredicate);
	}

	public Map<String, SemanticTypePredicate> getMappingMap() {
		return mappingMap;
	}

	private Map<String, SemanticTypePredicate> mappingMap = new HashMap<String, SemanticTypePredicate>();
	private static final Logger logger = LoggerFactory.getLogger(SemanticTypeConceptPredicateMapper.class);

}

class SemanticTypePredicate {
	public static final int SUB_PROP = 1;
	public static final int EQ_PROP = 2;

	private String from;
	private int relationship;
	private String relatedTo;

	public int getRelationship() {
		return relationship;
	}

	public String getRelatedTo() {
		return relatedTo;
	}

	public String getFrom() {
		return from;
	}

	public SemanticTypePredicate(String from, int relationship, String relatedTo) {
		super();
		this.from = from;
		this.relationship = relationship;
		this.relatedTo = relatedTo;
	}

}
