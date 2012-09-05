package org.biosemantics.eviped.tools.service.attribute;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

public class PatternReaderUtility {

	public static final List<Pattern> readPatternsFromFile(String regexFile, boolean caseSensitive)
			throws FileNotFoundException, IOException {
		List<Pattern> patterns = new ArrayList<Pattern>();
		Properties properties = new Properties();
		properties.load(new FileInputStream(regexFile));
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String value = (String) entry.getValue();
			Pattern pattern = null;
			if (caseSensitive) {
				pattern = Pattern.compile(value);
			} else {
				pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			}
			patterns.add(pattern);
		}
		return patterns;
	}

}
