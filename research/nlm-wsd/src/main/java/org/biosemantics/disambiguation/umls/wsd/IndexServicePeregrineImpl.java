package org.biosemantics.disambiguation.umls.wsd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.biosemantics.peregrine.rmi.PeregrineRmiManager;
import org.biosemantics.vo.peregrine.WsResultConcept;
import org.biosemantics.vo.peregrine.WsTerm;
import org.biosemantics.vo.peregrine.WsWord;
import org.erasmusmc.ids.DatabaseID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexServicePeregrineImpl implements IndexService {
	private static final int PEREGRINE_RMI_PORT = 1011;
	private static final String PEREGRINE_RMI_SERVER = "mi-bios1";
	private static final boolean PEREGRINE_DISAMBIGUATION = false;

	private PeregrineRmiManager peregrineRmiManager;
	private static final Logger logger = LoggerFactory.getLogger(IndexServicePeregrineImpl.class);

	public IndexServicePeregrineImpl() throws Exception {
		peregrineRmiManager = new PeregrineRmiManager(PEREGRINE_RMI_SERVER, PEREGRINE_RMI_PORT,
				PEREGRINE_DISAMBIGUATION);
	}

	// public Collection<String> getNonAmbiguousConcepts(String text) {
	// List<String> cuis = new ArrayList<String>();
	// WsFingerPrint wsFingerPrint = getFingerprint(text);
	// for (int i = 0; i < wsFingerPrint.getResultConcept().size(); i++) {
	// WsResultConcept wsResultConcept = wsFingerPrint.getResultConcept().get(i);
	// List<WsResultConcept> otherResultConcepts = new ArrayList<WsResultConcept>(wsFingerPrint.getResultConcept());
	// otherResultConcepts.remove(i);
	// boolean isDuplicate = false;
	// if (!otherResultConcepts.isEmpty()) {
	// for (WsTerm wsTerm : wsResultConcept.getTerm()) {
	// for (WsWord wsWord : wsTerm.getWord()) {
	// for (WsResultConcept otherConcept : otherResultConcepts) {
	// for (WsTerm otherTerm : otherConcept.getTerm()) {
	// for (WsWord otherWord : otherTerm.getWord()) {
	// if (otherWord.getLength() == wsWord.getLength()
	// && otherWord.getStartPosition() == wsWord.getStartPosition()
	// && otherWord.getText().equals(wsWord.getText())) {
	// isDuplicate = true;
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// if (!isDuplicate) {
	// for (WsExternalId wsExternalId : wsResultConcept.getExternalIdList()) {
	// if (wsExternalId.getDatabaseAbbreviation().equals("UMLS")) {
	// String cui = wsExternalId.getId();
	// cuis.add(cui);
	// }
	// }
	// }
	//
	// }
	// return cuis;
	// }

	public Collection<String> index(String text) {
		List<WsResultConcept> wsResultConcepts = peregrineRmiManager.index(text);
		logger.info("total concepts found in text: {}", wsResultConcepts.size());
		Collection<WsResultConcept> purgedConcepts = purgeAmbiguousConcepts(wsResultConcepts);
		logger.info("total concepts remaining after purgeAmbiguousConcepts: {}", purgedConcepts.size());
		Collection<String> cuis = conceptsToCuis(purgedConcepts);
		logger.info("total cuis available: {}", cuis.size());
		return cuis;
	}

	private Collection<String> conceptsToCuis(Collection<WsResultConcept> purgedConcepts) {
		List<String> cuis = new ArrayList<String>();
		for (WsResultConcept wsResultConcept : purgedConcepts) {
			List<DatabaseID> databaseIDs = peregrineRmiManager.getOntologyClient().getDatabaseIDsForConcept(
					wsResultConcept.getConceptId());
			for (DatabaseID databaseID : databaseIDs) {
				if (databaseID.database.equalsIgnoreCase("UMLS")) {
					cuis.add(databaseID.ID);
					break;
				}
			}
		}
		return cuis;
	}

	// if we have a word with multiple concepts ignore that word.
	private List<WsResultConcept> purgeAmbiguousConcepts(List<WsResultConcept> wsResultConcepts) {
		List<WsResultConcept> purgedConcepts = new ArrayList<WsResultConcept>();
		int ctr = 0;
		for (WsResultConcept wsResultConcept : wsResultConcepts) {
			List<WsResultConcept> otherResultConcepts = new ArrayList<WsResultConcept>(wsResultConcepts);
			// remove existing concept
			otherResultConcepts.remove(ctr);
			boolean isDuplicate = false;

			if (!otherResultConcepts.isEmpty()) {
				for (WsTerm wsTerm : wsResultConcept.getTerm()) {
					for (WsWord wsWord : wsTerm.getWord()) {
						for (WsResultConcept otherConcept : otherResultConcepts) {
							for (WsTerm otherTerm : otherConcept.getTerm()) {
								for (WsWord otherWord : otherTerm.getWord()) {
									if (otherWord.getLength() == wsWord.getLength()
											&& otherWord.getStartPosition() == wsWord.getStartPosition()
											&& otherWord.getText().equals(wsWord.getText())) {
										isDuplicate = true;
									}
								}
							}
						}
					}
				}
			}//
			if (!isDuplicate) {
				purgedConcepts.add(wsResultConcept);
			}
		}
		return purgedConcepts;
	}

	public static void main(String[] args) throws Exception {
		IndexServicePeregrineImpl peregrineFingerprintReader = new IndexServicePeregrineImpl();
		Collection<String> cuis = peregrineFingerprintReader.index(" malaria is cold");
		logger.info("{}", cuis);
	}

}
