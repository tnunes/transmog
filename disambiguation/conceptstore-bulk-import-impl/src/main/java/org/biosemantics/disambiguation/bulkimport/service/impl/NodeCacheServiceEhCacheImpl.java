package org.biosemantics.disambiguation.bulkimport.service.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.biosemantics.disambiguation.bulkimport.service.NodeCacheService;
import org.slf4j.Logger;

public class NodeCacheServiceEhCacheImpl implements NodeCacheService {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(NodeCacheServiceEhCacheImpl.class);
	private Cache labelCache;
	private Cache notationCache;

	public void init() {
		CacheManager.create();
		labelCache = CacheManager.getInstance().getCache("labelCache");
		notationCache = CacheManager.getInstance().getCache("notationCache");
		logger.info("{} and {} caches initied on init()", new Object[] { labelCache, notationCache });
	}

	@Override
	public void addLabel(String text, String language, long nodeId) {
		Element element = new Element(text + language, nodeId);
		labelCache.put(element);

	}

	@Override
	public void addNotation(String domainUuid, String code, long nodeId) {
		Element element = new Element(domainUuid + code, nodeId);
		notationCache.put(element);
	}

	@Override
	public long getLabelNodeId(String text, String language) {
		Element element = labelCache.get(text + language);
		if (element != null) {
			return (Long) element.getValue();
		}
		return -1;
	}

	@Override
	public long getNotationNodeId(String domainUuid, String code) {
		Element element = notationCache.get(domainUuid + code);
		if (element != null) {
			return (Long) element.getValue();
		}
		return -1;
	}

	public void destroy() {
		CacheManager.getInstance().shutdown();
		logger.info("eh cache shutdown called on destroy()");
	}

}
