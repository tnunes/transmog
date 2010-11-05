package org.biosemantics.disambiguation.datasource.umls;

import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Language;

public class DataSourceCommonUtility {

	public static final Map<String, Language> iso6392LanguageMap = new HashMap<String, Language>();

	static {
		for (Language language : Language.values()) {
			iso6392LanguageMap.put(language.getIso6392Code(), language);
		}
	}

	public static Language getLanguageByIso6392Code(final String iso6392Code) {
		return iso6392LanguageMap.get(iso6392Code);
	}

}
