package org.biosemantics.conceptstore.dataimport;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataImportConfigReader {

	private Properties properties = new Properties();

	public void init() throws IOException {

		InputStream is = this.getClass().getClassLoader().getResourceAsStream("dataimport-config.properties");
		if (is == null) {
			logger.info("predicate-mapping.txt file found");
		} else {
			properties.load(is);
			logger.info("loaded config {}", properties);
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public String getValue(String key) {
		return properties.getProperty(key);
	}

	private static final Logger logger = LoggerFactory.getLogger(DataImportConfigReader.class);

	public static void main(String[] args) throws IOException {
		DataImportConfigReader configReader = new DataImportConfigReader();
		configReader.init();
	}
}
