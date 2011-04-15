package org.biosemantics.disambiguation.bulkimport.service.impl;

import java.io.File;
import java.util.List;

import org.biosemantics.disambiguation.bulkimport.service.BulkImportNodeCache;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;

public class BulkImportNodeCacheDb4oImpl implements BulkImportNodeCache {

	private ObjectContainer db;
	private String cacheDir;
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BulkImportNodeCacheDb4oImpl.class);
	private static final String DB_F1ILE_NAME = "cache.yup";

	@Required
	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}

	public void init() {
		logger.info("init called.");
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		configuration.common().objectClass(LabelNodeImpl.class).objectField("language").indexed(true);
		configuration.common().objectClass(LabelNodeImpl.class).objectField("text").indexed(true);
		configuration.common().objectClass(NotationNodeImpl.class).objectField("domainUuid").indexed(true);
		configuration.common().objectClass(NotationNodeImpl.class).objectField("code").indexed(true);
		db = Db4oEmbedded.openFile(configuration, cacheDir + File.separator + DB_F1ILE_NAME);
		logger.info("db started.");
	}

	@Override
	public void addLabel(String text, String language, long nodeId) {
		LabelNodeImpl labelNodeImpl = new LabelNodeImpl(text, language, nodeId);

	}

	@Override
	public void addNotation(String domainUuid, String code, long nodeId) {
		NotationNodeImpl notationNodeImpl = new NotationNodeImpl(domainUuid, code, nodeId);

	}

	@Override
	public long getLabelNodeId(final String text, final String language) {
		List<LabelNodeImpl> labelNodes = db.query(new Predicate<LabelNodeImpl>() {
			public boolean match(LabelNodeImpl labelNodeImpl) {
				return labelNodeImpl.getLanguage().equals(language) && labelNodeImpl.getText().equals(text);
			}
		});
		if (labelNodes.size() == 1) {
			return labelNodes.get(0).getNodeId();
		} else {
			if (labelNodes.size() > 1) {
				logger.error("{} labelNodes found for a language {} and text {} pair", new Object[] {
						labelNodes.size(), language, text });
			}
			return -1;
		}

	}

	@Override
	public long getNotationNodeId(final String domainUuid, final String code) {
		List<NotationNodeImpl> notationNodes = db.query(new Predicate<NotationNodeImpl>() {
			public boolean match(NotationNodeImpl nodeImpl) {
				return nodeImpl.getDomainUuid().equals(domainUuid) && nodeImpl.getCode().equals(code);
			}
		});
		if (notationNodes.size() == 1) {
			return notationNodes.get(0).getNodeId();
		} else {
			if (notationNodes.size() > 1) {
				logger.error("{} notation nodes found for a domainUuid {} and code {} pair", new Object[] {
						notationNodes.size(), domainUuid, code });
			}
			return -1;
		}

	}

	public void destroy() {
		logger.info("destroy called.");
		db.close();
		logger.info("db closed.");
	}
}
