package org.biosemantics.wsd.datasource.umls;

import java.io.*;
import java.util.*;

import org.slf4j.*;

import au.com.bytecode.opencsv.*;


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
				String related = columns[2].trim();
				SemanticTypePredicate semanticTypePredicate = null;
				if (strRelationship.equalsIgnoreCase("subProp")) {
					semanticTypePredicate = new SemanticTypePredicate(SemanticTypePredicate.SUB_PROP, related);
				} else if (strRelationship.equalsIgnoreCase("eqProp")) {
					semanticTypePredicate = new SemanticTypePredicate(SemanticTypePredicate.EQ_PROP, related);
				}
				// add original name
				mappingMap.put(columns[0].trim(), semanticTypePredicate);
				// add inverse name
				mappingMap.put(columns[4].trim(), semanticTypePredicate);
			}
			logger.info("{} cuis ignored.", mappingMap.size());
			csvReader.close();
		}
	}

	public SemanticTypePredicate getMappedSemanticTypePredicate(String conceptPredicate) {
		return mappingMap.get(conceptPredicate);
	}

	private Map<String, SemanticTypePredicate> mappingMap = new HashMap<String, SemanticTypePredicate>();
	private static final Logger logger = LoggerFactory.getLogger(SemanticTypeConceptPredicateMapper.class);

}

class SemanticTypePredicate {
	public static final int SUB_PROP = 1;
	public static final int EQ_PROP = 2;

	private int relationship;
	private String relatedTo;

	public int getRelationship() {
		return relationship;
	}

	public String getRelatedTo() {
		return relatedTo;
	}

	public SemanticTypePredicate(int relationship, String relatedTo) {
		super();
		this.relationship = relationship;
		this.relatedTo = relatedTo;
	}

}
