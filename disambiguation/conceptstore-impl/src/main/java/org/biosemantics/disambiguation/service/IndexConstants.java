package org.biosemantics.disambiguation.service;

public abstract class IndexConstants {
	// index name that contains the attributes for all concepts like Labeltext etc.
	public static final String NODE_INDEX_NAME = "concept_index";
	// index name that contains the attributes for relationships like uuid.
	public static final String RELATIONSHIP_INDEX_NAME = "relationship_index";
	// index name that contains the full text index for a concept
	public static final String FULL_TXT_INDEX_NAME = "concept_full_text_index";
	//index name that contains attributes for relationship
	

	public static final String LABEL_TXT_INDEX_KEY = "label_text";
	public static final String NOTATION_CODE_INDEX_KEY = "notation_code";
	public static final String CONCEPT_ID_INDEX_KEY = "concept_uuid";
	
	public static final String FULL_TEXT_SEPARATOR = " ";
	public static final String CONCEPT_FULL_TEST_KEY = "concept_full_text";
	public static final String RELATIONSHIP_INDEX_KEY = "relationship_uuid";

}