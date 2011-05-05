package org.biosemantics.datasource.umls.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * Hashmap based cache implementation. Fastest but with a huge memory requirement tradeoff.
 * 
 * @author bhsingh
 * 
 */
public class UmlsCacheServiceImpl implements UmlsCacheService {

	private final Map<String, String> keyValueMap;
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UmlsCacheServiceImpl.class);

	public UmlsCacheServiceImpl() {
		keyValueMap = new HashMap<String, String>();
	}

	public void init() {
		// accessDb4o
		// EmbeddedConfiguration embeddedConfiguration = Db4oEmbedded.newConfiguration();
		// embeddedConfiguration.common().messageLevel(1);
		// embeddedConfiguration.common().callConstructors(true);
		// embeddedConfiguration.common().objectClass(KeyValue.class).objectField("key").indexed(true);
		// db = Db4oEmbedded.openFile(embeddedConfiguration, db4oFileName);
		// logger.info("umls cache db4o started.");
	}

	public void destroy() {
		keyValueMap.clear();
	}

//	private String getValueFromDatabase(final String key) {
//		long start = System.currentTimeMillis();
//		List<KeyValue> list = db.query(new Predicate<KeyValue>() {
//			public boolean match(KeyValue candidate) {
//				return candidate.getKey().equals(key);
//			}
//		});
//		long time = System.currentTimeMillis() - start;
//		if (time > 50) {
//			logger.info("slow query key:{} time:{}", new Object[] { key, time });
//		}
//		logger.debug("nq query key:{} in {}(ms)", new Object[] { key, time });
//
//		if (list.size() == 1) {
//			return list.get(0).getValue();
//		} else {
//			if (list.isEmpty()) {
//				return null;
//			} else {
//				logger.error("{}", list);
//				throw new IllegalStateException("list size > 1: multiple keys in datbase for " + key);
//			}
//		}
//	}

	@Override
	public void add(KeyValue keyValue) {
		String oldValue = keyValueMap.put(keyValue.getKey(), keyValue.getValue());
		if (!StringUtils.isBlank(oldValue)) {
			logger.error("duplicate key inserted in cache:{} old value is:{}", new Object[] { keyValue, oldValue });
		}
	}

	@Override
	public String getValue(String key) {
		return keyValueMap.get(key);
	}
}
