package org.biosemantics.disambiguation.droid.umls.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.query.Predicate;

public class IntermediateCacheImpl implements IntermediateCache {

	private ObjectServer server;
	private ObjectContainer client;
	private static final Logger logger = LoggerFactory.getLogger(IntermediateCacheImpl.class);

	public void init() {
		ServerConfiguration serverConfiguration = Db4oClientServer.newServerConfiguration();
		serverConfiguration.common().messageLevel(1);
		// serverConfiguration.common().objectClass(CuiConceptId.class).objectField("cui").indexed(true);
		serverConfiguration.common().objectClass(LabelNode.class).objectField("sui").indexed(true);
		serverConfiguration.common().objectClass(NotationNode.class).objectField("domain").indexed(true);
		serverConfiguration.common().objectClass(NotationNode.class).objectField("code").indexed(true);
		server = Db4oClientServer.openServer(serverConfiguration, "./cache/cache.db4o", 0);
		client = server.openClient();
	}

//	@Override
//	public boolean addToCache(CuiConceptId cuiConceptId) {
//		try {
//			client.store(cuiConceptId);
//			return true;
//		} catch (Exception e) {
//			logger.info("cannot add object to cache", e);
//			return false;
//		}
//
//	}
//
//	@Override
//	public CuiConceptId getByCui(final String cui) {
//		List<CuiConceptId> cuiConceptIds = client.query(new Predicate<CuiConceptId>() {
//			private static final long serialVersionUID = -4618020293845815708L;
//
//			public boolean match(CuiConceptId match) {
//				return match.getCui().equals(cui);
//			}
//		});
//		if (cuiConceptIds == null || cuiConceptIds.isEmpty()) {
//			logger.warn("cannot find cui {} in cache", cui);
//			return null;
//		} else {
//			return cuiConceptIds.get(0);
//		}
//	}

	public void destroy() {
		client.close();
		server.close();
	}

	@Override
	public long getLabelNodeId(final String sui) {
		long labelNodeId = 0;
		List<LabelNode> labelNodes = client.query(new Predicate<LabelNode>() {

			private static final long serialVersionUID = -3806232034191239013L;

			public boolean match(LabelNode match) {
				return match.getSui().equals(sui);
			}
		});
		if (CollectionUtils.isEmpty(labelNodes)) {
			//logger.warn("label node id not found for sui {}", sui);

		} else {
			if (labelNodes.size() == 1) {
				labelNodeId = labelNodes.get(0).getNodeId();
			} else {
				logger.warn("size of returned labelIds is not 1 for sui {} size {}",
						new Object[] { sui, labelNodes.size() });
			}
		}
		return labelNodeId;
	}

	@Override
	public void addLabelNode(LabelNode labelNode) {
		client.store(labelNode);
	}

	@Override
	public long getNotationNodeId(final String domain, final String code) {
		long notationNodeId = 0;
		List<NotationNode> notationNodes = client.query(new Predicate<NotationNode>() {
			private static final long serialVersionUID = 3579468422313248819L;

			public boolean match(NotationNode match) {
				return (match.getDomain().equals(domain) && match.getCode().equals(code));
			}
		});
		if (CollectionUtils.isEmpty(notationNodes)) {
			//logger.warn("notation node id not found for domain {} and code {} ", new Object[] { domain, code });

		} else {
			if (notationNodes.size() == 1) {
				notationNodeId = notationNodes.get(0).getNodeId();
			} else {
				logger.warn("size of notation node id not 1 for domain {} and code {} size {}", new Object[] { domain,
						code, notationNodes.size() });
			}
		}
		return notationNodeId;
	}

	@Override
	public void addNotationNode(NotationNode notationNode) {
		client.store(notationNode);

	}

}
