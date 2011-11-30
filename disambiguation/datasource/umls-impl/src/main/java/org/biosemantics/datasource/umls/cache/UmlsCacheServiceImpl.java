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
	private final Map<String, String> domainMap;
	private final Map<String, String> relationshipMap;
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UmlsCacheServiceImpl.class);

	public UmlsCacheServiceImpl() {
		keyValueMap = new HashMap<String, String>();
		domainMap = new HashMap<String, String>();
		relationshipMap = new HashMap<String, String>();
	}

	public void destroy() {
		keyValueMap.clear();
	}

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

	@Override
	public void addDomainNode(KeyValue keyValue) {
		String oldValue = domainMap.put(keyValue.getKey(), keyValue.getValue());
		if (!StringUtils.isBlank(oldValue)) {
			logger.error("duplicate key inserted in domain cache:{} old value is:{}",
					new Object[] { keyValue, oldValue });
		}
	}

	@Override
	public String getDomainNode(String key) {
		return domainMap.get(key);
	}

	@Override
	public void addRelationship(String key) {
		String oldValue = relationshipMap.put(key, null);
		if (!StringUtils.isBlank(oldValue)) {
			logger.error("duplicate key inserted in relationship cache:{} old value is:{}", new Object[] { key,
					oldValue });
		}

	}

	@Override
	public boolean getRelationship(String key) {
		return relationshipMap.containsKey(key);
	}
}
