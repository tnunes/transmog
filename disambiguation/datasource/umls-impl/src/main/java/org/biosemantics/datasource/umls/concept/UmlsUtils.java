package org.biosemantics.datasource.umls.concept;

import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.conceptstore.common.domain.SemanticRelationshipCategory;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;

public abstract class UmlsUtils {

	private static final String SCR = "scr";
	public static final String DEFAULT_SAB = "MTH";

	public static Language getLanguage(String lat) {
		lat = lat.toLowerCase();
		// http://www.loc.gov/standards/iso639-2/php/code_changes.php SCR has
		// been deprecated
		if (lat.equalsIgnoreCase(SCR)) {
			lat = LanguageImpl.HR.getIso6392Code();
		}
		Language language = LanguageUtility.getLanguageByIso6392Code(lat);
		return language;
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
	 * 
	 * AQ Allowed qualifier CHD has child relationship in a Metathesaurus
	 * source vocabulary DEL Deleted concept PAR has parent relationship in
	 * a Metathesaurus source vocabulary QB can be qualified by. RB has a
	 * broader relationship RL the relationship is similar or "alike". the
	 * two concepts are similar or "alike". In the current edition of the
	 * Metathesaurus, most relationships with this attribute are mappings
	 * provided by a source, named in SAB and SL; hence concepts linked by
	 * this relationship may be synonymous, i.e. self-referential: CUI1 =
	 * CUI2. In previous releases, some MeSH Supplementary Concept
	 * relationships were represented in this way. RN has a narrower
	 * relationship RO has relationship other than synonymous, narrower, or
	 * broader RQ related and possibly synonymous. RU Related, unspecified
	 * SIB has sibling relationship in a Metathesaurus source vocabulary. SY
	 * source asserted synonymy. XR Not related, no mapping Empty
	 * relationship
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

}