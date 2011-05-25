package org.biosemantics.datasource.umls.concept;

import java.util.Set;

import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;

public abstract class UmlsUtils {

	private static final String SCR = "scr";
	public static final String DEFAULT_SAB = "MTH";
	public static final String SEPERATOR = " ";
	public static final String NOCODE = "NOCODE";
	public static final int BATCH_SIZE = 10000;

	public static Language getLanguage(String lat) {
		String lowerCaseLat = lat.toLowerCase();
		// http://www.loc.gov/standards/iso639-2/php/code_changes.php SCR has
		// been deprecated
		if (lat.equalsIgnoreCase(SCR)) {
			lowerCaseLat = LanguageImpl.HR.getIso6392Code();
		}
		return LanguageUtility.getLanguageByIso6392Code(lowerCaseLat);
	}

	public static LabelType getLabelType(String ts, String isPref, String stt) {
		if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
			return LabelType.PREFERRED;
		} else {
			return LabelType.ALTERNATE;
		}
	}

	// available values for rel in UMLS:
	// http://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/abbreviations.html
	/*
	REL   (Relationship)
	AQ	Allowed qualifier
	CHD	has child relationship in a Metathesaurus source vocabulary
	DEL	Deleted concept
	PAR	has parent relationship in a Metathesaurus source vocabulary
	QB	can be qualified by.
	RB	has a broader relationship
	RL	the relationship is similar or "alike". the two concepts are similar or "alike". In the current edition of the Metathesaurus, most relationships with this attribute are mappings provided by a source, named in SAB and SL; hence concepts linked by this relationship may be synonymous, i.e. self-referential: CUI1 = CUI2. In previous releases, some MeSH Supplementary Concept relationships were represented in this way.
	RN	has a narrower relationship
	RO	has relationship other than synonymous, narrower, or broader
	RQ	related and possibly synonymous.
	RU	Related, unspecified
	SIB	has sibling relationship in a Metathesaurus source vocabulary.
	SY	source asserted synonymy.
	XR	Not related, no mapping
	Empty relationship 
	 */
	public static SemanticRelationshipCategory getConceptRelationshipType(String rel) {
		SemanticRelationshipCategory semanticRelationshipCategory = SemanticRelationshipCategory.RELATED;
		String relUpper = rel.toUpperCase().trim();
		if (relUpper.equals("CHD") || relUpper.equals("RN")) {
			semanticRelationshipCategory = SemanticRelationshipCategory.HAS_NARROWER_CONCEPT;
		} else if (relUpper.equals("PAR") || relUpper.equals("RB")) {
			semanticRelationshipCategory = SemanticRelationshipCategory.HAS_BROADER_CONCEPT;
		}
		return semanticRelationshipCategory;
	}

	public static String setToString(Set<String> fullText) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : fullText) {
			stringBuilder.append(string).append(SEPERATOR);
		}
		return stringBuilder.toString();
	}

}
