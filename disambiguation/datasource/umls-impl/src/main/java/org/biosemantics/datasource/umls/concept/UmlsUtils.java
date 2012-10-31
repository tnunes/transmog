package org.biosemantics.datasource.umls.concept;

import java.util.Set;

import org.biosemantics.conceptstore.common.domain.ConceptRelationshipType;
import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UmlsUtils {

	private static final String SCR = "scr";
	public static final String DEFAULT_SAB = "MTH";
	public static final String SEPERATOR = " ";
	public static final String NOCODE = "NOCODE";
	public static final int BATCH_SIZE = 10000;
	public static final int MAX_RLSP_WEIGHT = 100;
	private static final Logger logger = LoggerFactory.getLogger(UmlsUtils.class);

	/*
	 * EN("en", "eng"), EU("eu", "baq"), CS("cs", "cze"), DA("da", "dan"), NL("nl", "dut"), FI("fi", "fin"), FR("fr",
			"fre"), DE("de", "ger"), HE("he", "heb"), HU("hu", "hun"), IT("it", "ita"), JA("ja", "jpn"), KO("ko", "kor"), LV(
			"lv", "lav"), NO("no", "nor"), PT("pt", "por"), RU("ru", "rus"), HR("hr", "hrv"), ES("es", "spa"), SV("sv",
			"swe");
	 */
	// FIXME
	public static Language getLanguage(String lat) {
		if (lat.equalsIgnoreCase("eng")) {
			return Language.EN;
		} else if (lat.equalsIgnoreCase("baq")) {
			return Language.EU;
		} else if (lat.equalsIgnoreCase("cze")) {
			return Language.CS;
		} else if (lat.equalsIgnoreCase("dan")) {
			return Language.DA;
		} else if (lat.equalsIgnoreCase("dut")) {
			return Language.NL;
		} else if (lat.equalsIgnoreCase("fin")) {
			return Language.FI;
		} else if (lat.equalsIgnoreCase("fre")) {
			return Language.FR;
		} else if (lat.equalsIgnoreCase("ger")) {
			return Language.DE;
		} else if (lat.equalsIgnoreCase("heb")) {
			return Language.HE;
		} else if (lat.equalsIgnoreCase("hun")) {
			return Language.HU;
		} else if (lat.equalsIgnoreCase("ita")) {
			return Language.IT;
		} else if (lat.equalsIgnoreCase("jpn")) {
			return Language.JA;
		} else if (lat.equalsIgnoreCase("kor")) {
			return Language.KO;
		} else if (lat.equalsIgnoreCase("lav")) {
			return Language.LV;
		} else if (lat.equalsIgnoreCase("nor")) {
			return Language.NO;
		} else if (lat.equalsIgnoreCase("por")) {
			return Language.PT;
		} else if (lat.equalsIgnoreCase("rus")) {
			return Language.RU;
		} else if (lat.equalsIgnoreCase("hrv")) {
			return Language.HR;
		} else if (lat.equalsIgnoreCase("spa")) {
			return Language.ES;
		} else if (lat.equalsIgnoreCase("swe")) {
			return Language.SV;
			// old code for HRV is SCR
		} else if (lat.equalsIgnoreCase(SCR)) {
			return Language.HR;
		}
		// default

		return Language.EN;
	}

	public static LabelType getLabelType(String ts, String isPref, String stt) {
		if (ts.equalsIgnoreCase("P") && isPref.equalsIgnoreCase("Y") && stt.equalsIgnoreCase("PF")) {
			return LabelType.PREFERRED;
		} else {
			return LabelType.ALTERNATE;
		}
	}

	// AQ Allowed qualifier
	// CHD has child relationship in a Metathesaurus source vocabulary
	// DEL Deleted concept
	// PAR has parent relationship in a Metathesaurus source vocabulary
	// QB can be qualified by.
	// RB has a broader relationship
	// RL the relationship is similar or "alike". the two concepts are similar or "alike". In the current edition of the
	// Metathesaurus, most relationships with this attribute are mappings provided by a source, named in SAB and SL;
	// hence concepts linked by this relationship may be synonymous, i.e. self-referential: CUI1 = CUI2. In previous
	// releases, some MeSH Supplementary Concept relationships were represented in this way.
	// RN has a narrower relationship
	// RO has relationship other than synonymous, narrower, or broader
	// RQ related and possibly synonymous.
	// RU Related, unspecified
	// SIB has sibling relationship in a Metathesaurus source vocabulary.
	// SY source asserted synonymy.
	// XR Not related, no mapping.
	// Empty relationship

	public static ConceptRelationshipType getConceptRelationshipType(String rel) {
		ConceptRelationshipType semanticRelationshipCategory = ConceptRelationshipType.RELATED;
		String relUpper = rel.toUpperCase().trim();
		if (relUpper.equals("CHD") || relUpper.equals("RB") || relUpper.equals("RO") || relUpper.equals("SIB")
				|| relUpper.equals("SY")) {
			if (relUpper.equals("CHD")) {
				semanticRelationshipCategory = ConceptRelationshipType.HAS_CHILD_CONCEPT;
			} else if (relUpper.equals("RB")) {
				semanticRelationshipCategory = ConceptRelationshipType.HAS_BROADER_CONCEPT;
			}
		} else {
			logger.error("REL  = {} received. Illegal value for relationships.", rel);
			throw new IllegalArgumentException();
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

	public static double getRlspWeight(String rel) {
		String relUpper = rel.toUpperCase().trim();
		if (relUpper.equals("CHD") || relUpper.equals("RB")) {
			return MAX_RLSP_WEIGHT;
		} else if (relUpper.equals("RO") || relUpper.equals("SIB") || relUpper.equals("SY")) {
			return MAX_RLSP_WEIGHT / 2;
		} else {
			logger.error("REL  = {} received. Illegal value for relationships.", rel);
			throw new IllegalArgumentException();
		}
	}
	
	

}
