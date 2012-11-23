package org.biosemantics.wsd.datasource.umls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {

	private Properties properties = new Properties();

	public void init() throws IOException {

		InputStream is = this.getClass().getClassLoader().getResourceAsStream("umls-import-config.properties");
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

	private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
	
	
	public static void main(String[] args) throws IOException {
		ConfigReader configReader = new ConfigReader();
		configReader.init();
	}
}
