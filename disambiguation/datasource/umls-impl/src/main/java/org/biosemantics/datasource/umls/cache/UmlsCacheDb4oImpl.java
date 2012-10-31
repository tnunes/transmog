package org.biosemantics.datasource.umls.cache;

import com.db4o.Db4o;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.config.EmbeddedConfiguration;

public class UmlsCacheDb4oImpl implements UmlsCacheService {

	private final ObjectContainer db;

	public UmlsCacheDb4oImpl(String databaseFileName) {
//		EmbeddedConfiguration embeddedConfiguration = Db4oEmbedded.newConfiguration();
//		embeddedConfiguration.
		db = Db4oEmbedded.openFile(databaseFileName);
		
	}

	@Override
	public void add(KeyValue keyValue) {
		db.store(keyValue);

	}

	@Override
	public String getValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addDomainNode(KeyValue keyValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDomainNode(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRelationship(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getRelationship(String key) {
		// TODO Auto-generated method stub
		return false;
	}

}
