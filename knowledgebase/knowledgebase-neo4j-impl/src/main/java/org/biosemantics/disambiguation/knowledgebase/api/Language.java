package org.biosemantics.disambiguation.knowledgebase.api;

/**
 * 
 * http://www.loc.gov/standards/iso639-2/php/code_list.php
 * 
 */
public enum Language {
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
	SCR 	Croatian // INCORRECT on web page above?
	SPA 	Spanish
	SWE 	Swedish
	 */
	EN("en", "eng"), EU("eu", "baq"), CS("cs", "cze"), DA("da", "dan"), NL("nl", "dut"), FI("fi", "fin"), FR("fr",
			"fre"), DE("de", "ger"), HE("he", "heb"), HU("hu", "hun"), IT("it", "ita"), JA("ja", "jpn"), KO("ko", "kor"), LV(
			"lv", "lav"), NO("no", "nor"), PT("pt", "por"), RU("ru", "rus"), HR("hr", "hrv"), ES("es", "spa"), SV("sv",
			"swe");
	private String iso6391Code;
	private String iso62392Code;

	private Language(String iso6391Code, String iso6392Code) {
		this.iso6391Code = iso6391Code;
		this.iso62392Code = iso6392Code;
	}

	public String getIso6391Code() {
		return iso6391Code;
	}

	public String getIso62392Code() {
		return iso62392Code;
	}

}
