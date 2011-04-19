package org.biosemantics.datasource.umls.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;

public class UmlsCacheServiceEhCacheImpl implements UmlsCacheService {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UmlsCacheServiceEhCacheImpl.class);
	private Cache domainCache;
	private Cache cuiCache;
	private Cache predicateCache;
	private Cache conceptSchemeCache;

	public void init() {
		CacheManager.create();
		domainCache = CacheManager.getInstance().getCache("domainCache");
		cuiCache = CacheManager.getInstance().getCache("cuiCache");
		predicateCache = CacheManager.getInstance().getCache("predicateCache");
		conceptSchemeCache = CacheManager.getInstance().getCache("conceptSchemeCache");
		logger.info("{} caches found", CacheManager.getInstance().getCacheNames());
	}

	@Override
	public void addDomain(String domainName, String domainUuid) {
		Element element = new Element(domainName, domainUuid);
		domainCache.put(element);

	}

	@Override
	public String getDomainUuidByName(String domainName) {
		Element element = domainCache.get(domainName);
		if (element != null) {
			return (String) element.getValue();
		} else {
			logger.error("no domain uuid found for domainName {}", domainName);
			throw new IllegalStateException();
		}
	}

	@Override
	public void addCui(String cui, String conceptUuid) {
		Element element = new Element(cui, conceptUuid);
		cuiCache.put(element);

	}

	@Override
	public String getUuidforCui(String cui) {
		Element element = cuiCache.get(cui);
		if (element != null) {
			return (String) element.getValue();
		} else {
			logger.error("no uuid found for cui {}", cui);
			throw new IllegalStateException();
		}
	}

	@Override
	public void addPredicate(String text, String uuid) {
		Element element = new Element(text, uuid);
		predicateCache.put(element);

	}

	@Override
	public String getUuidForPredicateText(String text) {
		Element element = predicateCache.get(text);
		if (element != null) {
			return (String) element.getValue();
		} else {
			logger.error("no predicate uuid found for text {}", text);
			throw new IllegalStateException();
		}
	}

	public void destroy() {
		CacheManager.getInstance().shutdown();
		logger.info("eh cache shutdown called on destroy()");
	}

	@Override
	public void addConceptScheme(String text, String uuid) {
		Element element = new Element(text, uuid);
		conceptSchemeCache.put(element);

	}

	@Override
	public String getUuidforConceptSchemeText(String text) {
		Element element = conceptSchemeCache.get(text);
		if (element != null) {
			return (String) element.getValue();
		} else {
			logger.error("no conceptscheme uuid found for text {}", text);
			throw new IllegalStateException();
		}
	}

}
