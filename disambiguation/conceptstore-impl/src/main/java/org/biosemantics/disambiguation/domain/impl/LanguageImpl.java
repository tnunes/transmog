package org.biosemantics.disambiguation.domain.impl;

import org.biosemantics.conceptstore.common.domain.Language;

public enum LanguageImpl implements Language {

	/*
	 * BAQ 	Basque
	CZE 	Czech
	DAN 	Danish
	DUT 	Dutch
	ENG 	English
	FIN 	Finnish
	FRE 	French
	GER 	German
	HEB 	Hebrew
	HUN 	Hungarian
	ITA 	Italian
	JPN 	Japanese
	KOR 	Korean
	LAV 	Latvian
	NOR 	Norwegian
	POR 	Portuguese
	RUS 	Russian
	SCR 	Croatian // http://www.loc.gov/standards/iso639-2/php/code_changes.php SCR is deprecated
	SPA 	Spanish
	SWE 	Swedish
	 */
	EN("en", "eng"), EU("eu", "baq"), CS("cs", "cze"), DA("da", "dan"), NL("nl", "dut"), FI("fi", "fin"), FR("fr",
			"fre"), DE("de", "ger"), HE("he", "heb"), HU("hu", "hun"), IT("it", "ita"), JA("ja", "jpn"), KO("ko", "kor"), LV(
			"lv", "lav"), NO("no", "nor"), PT("pt", "por"), RU("ru", "rus"), HR("hr", "hrv"), ES("es", "spa"), SV("sv",
			"swe");
	private String iso6391Code;
	private String iso6392Code;

	private LanguageImpl(String iso6391Code, String iso6392Code) {
		this.iso6391Code = iso6391Code;
		this.iso6392Code = iso6392Code;
	}

	/**
	 * retrieves the ISO 639-1 code for this language
	 * 
	 * @return ISO 639-1 code for the language
	 */
	@Override
	public String getIso6391Code() {
		return iso6391Code;
	}

	/**
	 * retrieves the ISO 639-2 code for this language
	 * 
	 * @return ISO 639-2 code for the language
	 */
	@Override
	public String getIso6392Code() {
		return iso6392Code;
	}

	@Override
	public String getLabel() {
		return this.name();
	}
}
