package org.biosemantics.datasource.umls.concept;

import org.biosemantics.conceptstore.common.domain.LabelType;
import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;

public abstract class UmlsUtils {

	private static final String SCR = "scr";

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

}
