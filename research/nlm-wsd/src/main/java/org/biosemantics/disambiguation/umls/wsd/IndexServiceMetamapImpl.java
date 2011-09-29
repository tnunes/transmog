package org.biosemantics.disambiguation.umls.wsd;

import java.util.Collection;

import org.biosemantics.metamap.client.MappedCui;
import org.biosemantics.metamap.client.MetamapServiceImpl;

public class IndexServiceMetamapImpl implements IndexService {

	@Override
	public Collection<String> index(String text) throws Exception {
		MappedCui mappedCui = MetamapServiceImpl.INSTANCE.getMappedCui(text);
		return mappedCui.getPositiveCuis();
	}

}
