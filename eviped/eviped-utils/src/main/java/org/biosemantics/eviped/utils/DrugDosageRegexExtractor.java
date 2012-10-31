package org.biosemantics.eviped.utils;

import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrugDosageRegexExtractor {

	// private static final String SIMPLE_DIGIT = "(\\d+)";
	private static final String SPACE = "\\s";
	private static final String ALPHA = "[a-zA-Z]+";
	private static final String HYPHEN = "\\-";
	private static final String COMMA = ",";
	private static final String TO = "to";
	private static final String SLASH = "/";
	// private static final String TWO_ALPHA = "\\w\\w";
	private static final String DIGIT = "\\d+(\\.\\d{1,2})?";// http://stackoverflow.com/questions/308122/simple-regular-expression-for-a-decimal-with-a-precision-of-2

	private static final String CARDINAL_ONE = "one";
	private static final String CARDINAL_TWO = "two";
	private static final String CARDINAL_THREE = "three";
	private static final String CARDINAL_FOUR = "four";
	private static final String CARDINAL_FIVE = "five";
	private static final String CARDINAL_SIX = "six";
	private static final String CARDINAL_SEVEN = "seven";
	private static final String CARDINAL_EIGHT = "eight";
	private static final String CARDINAL_NINE = "nine";
	private static final String CARDINAL_ZERO = "zero";

	private static final String ORDIAL_ONE = "one";
	private static final String ORDIAL_TWO = "half";
	private static final String ORDIAL_THREE = "third";
	private static final String ORDIAL_FOUR = "fourth";
	private static final String ORDIAL_FIVE = "fifth";
	private static final String ORDIAL_SIX = "sixth";
	private static final String ORDIAL_SEVEN = "seventh";
	private static final String ORDIAL_EIGHT = "eighth";
	private static final String ORDIAL_NINE = "ninth";
	private static final String ORDIAL_ZERO = "zero";

	private static final String ALL_CARDINALS = "(" + CARDINAL_ONE + "|" + CARDINAL_TWO + "|" + CARDINAL_THREE + "|"
			+ CARDINAL_FOUR + "|" + CARDINAL_FIVE + "|" + CARDINAL_SIX + "|" + CARDINAL_SEVEN + "|" + CARDINAL_EIGHT
			+ "|" + CARDINAL_NINE + "|" + CARDINAL_ZERO + ")+";

	private static final String ALL_ORDIALS = "(" + ORDIAL_ONE + "|" + ORDIAL_TWO + "|" + ORDIAL_THREE + "|"
			+ ORDIAL_FOUR + "|" + ORDIAL_FIVE + "|" + ORDIAL_SIX + "|" + ORDIAL_SEVEN + "|" + ORDIAL_EIGHT + "|"
			+ ORDIAL_NINE + "|" + ORDIAL_ZERO + ")+";

	private static final Pattern DIGIT_ALPHA = Pattern.compile(DIGIT + ALPHA);
	private static final Pattern DIGIT_SPACE_ALPHA = Pattern.compile(DIGIT + SPACE + ALPHA);
	private static final Pattern DIGIT_SPACE_ALPHA_SLASH_ALPHA = Pattern.compile(DIGIT + SPACE + ALPHA + SLASH + ALPHA);
	private static final Pattern DIGIT_ALPHA_DIGIT_ALPHA = Pattern.compile(DIGIT + ALPHA + DIGIT + ALPHA);
	private static final Pattern DIGIT_HYPHEN_DIGIT = Pattern.compile(DIGIT + HYPHEN + DIGIT);
	private static final Pattern DIGIT_HYPHEN_ALPHA = Pattern.compile(DIGIT + HYPHEN + ALPHA);
	private static final Pattern DIGIT_COMMA_DIGIT = Pattern.compile(DIGIT + COMMA + DIGIT);
	private static final Pattern DIGIT_TO_DIGIT = Pattern.compile(DIGIT + SPACE + TO + SPACE + DIGIT,
			Pattern.CASE_INSENSITIVE);
	private static final Pattern DIGIT_SLASH_DIGIT = Pattern.compile(DIGIT + SLASH + DIGIT);
	private static final Pattern DIGIT_SLASH_DIGIT_ALPHA = Pattern.compile(DIGIT + SLASH + DIGIT + ALPHA);
	private static final Pattern DIGIT_ALPHA_SLASH_ALPHA = Pattern.compile(DIGIT + ALPHA + SLASH + ALPHA);
	private static final Pattern ALL_CARDINALS_PATTERN = Pattern.compile(ALL_CARDINALS, Pattern.CASE_INSENSITIVE);
	private static final Pattern CARDINALS_TO_CARDINALS = Pattern.compile(ALL_CARDINALS + SPACE + TO + SPACE
			+ ALL_CARDINALS, Pattern.CASE_INSENSITIVE);
	private static final Pattern CARDINALS_HYPHEN_ORDIALS = Pattern.compile(ALL_CARDINALS + HYPHEN + ALL_ORDIALS,
			Pattern.CASE_INSENSITIVE);

	private static final String TEXT = "one-third eight to seven one zero 0.4mg/spray is 500/50mg is 2/3 is 2 TO 3 1,000 Impala 2-3 2-liter 7mgx3d 7.5mgx3d 10mg and (10 mg/kg, n=6), buspirone (0.05 mg/kg, n=8), pimozide (1 mg/kg, n=8), doxapram (1 mg/kg, n=6), and control solutions on separate occasions. During the immobilization, partial pressures of oxygen (PaO(2), mmHg) and carbon dioxide (PaCO(2), mmHg), respiratory rate (breaths/min), ventilation (l/min), peripheral O(2) saturation (%), tidal volume (l), and respiratory exchange ratio were measured before and after injection of the experimental drugs.";

	public static void main(String[] args) {

		Matcher matcher = CARDINALS_HYPHEN_ORDIALS.matcher(TEXT);
		boolean found = false;
		while (matcher.find()) {
			System.err.println("" + matcher.group() + "\t" + matcher.start() + "\t" + matcher.end());

			found = true;
		}
	}

}
