package org.biosemantics.datasource.umls.concept;

import java.util.HashMap;
import java.util.Map;

import org.biosemantics.conceptstore.common.domain.Language;
import org.biosemantics.disambiguation.domain.impl.LanguageImpl;

public abstract class LanguageUtility {
	public static final Map<String, LanguageImpl> iso6392LanguageMap = new HashMap<String, LanguageImpl>();

	static {
		for (LanguageImpl language : LanguageImpl.values()) {
			iso6392LanguageMap.put(language.getIso6392Code(), language);
		}
	}

	public static Language getLanguageByIso6392Code(final String iso6392Code) {
		return iso6392LanguageMap.get(iso6392Code);
	}

}
