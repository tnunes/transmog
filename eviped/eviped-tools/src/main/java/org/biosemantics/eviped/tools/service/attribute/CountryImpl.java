package org.biosemantics.eviped.tools.service.attribute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.biosemantics.eviped.tools.service.Annotation;
import org.biosemantics.eviped.tools.service.AnnotationReaderImpl;
import org.biosemantics.eviped.tools.service.AnnotationTypeConstant;

public class CountryImpl implements AttributeExtractorService {

	private List<Country> countries = new ArrayList<Country>();
	private boolean caseSensitiveMatch;

	public CountryImpl(boolean caseSensitiveMatch) {
		this.caseSensitiveMatch = caseSensitiveMatch;
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			String iso = locale.getISO3Country();
			String code = locale.getCountry();
			String name = locale.getDisplayCountry();
			if (!"".equals(iso) && !"".equals(code) && !"".equals(name)) {
				countries.add(new Country(iso, code, name));
			}
		}

		Collections.sort(countries, new CountryComparator());
		for (Country country : countries) {
			System.out.println(country);
		}
	}


	@Override
	public List<Annotation> getAnnotations(String text, int sentenceNumber) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (Country country : countries) {
			String countryIso = country.getIso();
			String countryName = country.getName();
			if (caseSensitiveMatch) {
				countryIso = countryIso.toLowerCase();
				countryName = countryIso.toLowerCase();
				text = text.toLowerCase();
			}
			if (text.indexOf(countryName) != -1) {
				annotations.add(new Annotation(AnnotationTypeConstant.COUNTRY, text.indexOf(countryName), text
						.indexOf(countryName) + countryName.length(), sentenceNumber, countryName));
			}
			if (text.indexOf(countryIso) != -1) {
				annotations.add(new Annotation(AnnotationTypeConstant.COUNTRY, text.indexOf(countryIso), text
						.indexOf(countryIso) + countryIso.length(), sentenceNumber, countryIso));
			}
		}
		return annotations;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		AnnotationReaderImpl annotationReaderImpl = new AnnotationReaderImpl(
				"/Users/bhsingh/Desktop/annotation-all.txt");
		CountryImpl countryImpl = new CountryImpl(false);
		List<String> strings = annotationReaderImpl.getAnnotationTextByType("Country");
		for (String string : strings) {
			// System.err.println(string);
			List<Annotation> annotations = countryImpl.getAnnotations(string, 0);
			if (annotations != null && !annotations.isEmpty()) {
				for (Annotation annotation : annotations) {
					System.err.println(string + "\t" + annotation);
				}
			}
		}
	}
}

class CountryComparator implements Comparator<Country> {
	private Comparator comparator;

	CountryComparator() {
		comparator = Collator.getInstance();
	}

	public int compare(Country o1, Country o2) {
		return comparator.compare(o1.getName(), o2.getName());
	}

}

class Country {
	private String iso;
	private String code;
	private String name;

	Country(String iso, String code, String name) {
		this.iso = iso;
		this.code = code;
		this.name = name;
	}

	public String toString() {
		return iso + " - " + code + " - " + name.toUpperCase();
	}

	public String getIso() {
		return iso;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}
