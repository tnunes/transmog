package org.biosemantics.disambiguation.bulkimport.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.biosemantics.disambiguation.bulkimport.service.NodeCacheService;
import org.slf4j.Logger;

/**
 * Memory hog! allocate appox. 20GB memory for usage Fastest! < 1 hour for UMLS concept import
 * 
 * @author bharat
 * 
 */
public class NodeCacheServiceHashmapImpl implements NodeCacheService {

	private Map<String, Long> cache;
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(NodeCacheServiceHashmapImpl.class);

	public void init() {
		logger.info("cache inited");
		cache = new HashMap<String, Long>();
	}

	@Override
	public void addLabel(String text, String language, long nodeId) {
		cache.put(text + language, nodeId);

	}

	@Override
	public void addNotation(String domainUuid, String code, long nodeId) {
		cache.put(domainUuid + code, nodeId);

	}

	@Override
	public long getLabelNodeId(String text, String language) {
		if (cache.containsKey(text + language)) {
			return cache.get(text + language);
		} else {
			return -1;
		}

	}

	@Override
	public long getNotationNodeId(String domainUuid, String code) {
		if (cache.containsKey(domainUuid + code)) {
			return cache.get(domainUuid + code);
		} else {
			return -1;
		}
	}

	public void destroy() {
		cache = null;
		logger.info("cache destroyed");
	}

}
