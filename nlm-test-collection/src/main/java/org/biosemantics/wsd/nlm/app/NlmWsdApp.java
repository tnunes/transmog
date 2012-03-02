package org.biosemantics.wsd.nlm.app;

import java.util.ArrayList;
import java.util.List;

import org.biosemantics.wsd.ssi.SsiImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NlmWsdApp {

	private static final String TEXT = "Cold storage induces an endothelium-independent relaxation to hypoxia/reoxygenation in porcine coronary arteries. Tissues are often cold stored for physiological studies and for clinical transplantation. We report that cold storage induces a relaxation to reoxygenation after hypoxia (H/R) in de-endothelialized porcine coronary arteries. In fresh denuded arteries stimulated with U46619, H/R did not elicit relaxation. However, after overnight cold storage (4 degrees C), H/R elicited a transient relaxation with peak relaxation of 56 +/- 8% (n = 8), which was reproducible after 2 days of cold storage. The H/R relaxation was inhibited by methylene blue (10 microM) and LY83583 (10 microM), O2-hemoglobin (1 microM), or N(G)-methyl-L-arginine (0.2 mM), but neither N(G)-nitro-L-arginine (0.2 mM) nor cyclo-oxygenase inhibition was effective. Importantly, the H/R relaxation was attenuated by KCl (40 mM) or tetrabutylammonium chloride (5 mM), a non-selective inhibitor of K+ channels. Interestingly, authentic nitric oxide (NO)- or S-nitroso-N-acetylpenicillamine (SNAP)-induced relaxations were enhanced by cold storage in U46619 (0.1 microM) contractures. When tissues were contracted with KCl (40 mM), the enhancement in NO- or SNAP-induced relaxation by cold storage was markedly smaller than with U46619. Neither catalase (1,200 U/ml) nor 3-amino-triazole (50 mM), an inhibitor of catalase, affected the H/R relaxation. The duration of H/R relaxation also increased with the period of incubation at 37 degrees C in the organ bath. This was blocked by inhibition of NO synthesis or guanylate cyclase. Moreover, inhibition of protein synthesis with actinomycin D (0.1 microM) and cycloheximide (10 microM), or dexamethasone (1 microM), an inhibitor of NO synthase induction, blocked this increase in the duration of the H/R relaxation. The results suggest that in smooth muscle induction of NO pathway relaxation, which is in part mediated by K+ channels and inducible NO synthase, may be of importance to the understanding of ischemia/reperfusion responses in cold-stored arteries.";

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"nlm-test-collection-context.xml");
		applicationContext.registerShutdownHook();
		SsiImpl ssiImpl = applicationContext.getBean(SsiImpl.class);
		// MetamapClient metamapClient = applicationContext.getBean(MetamapClient.class);
		// MetamapIndexingResult metamapIndexingResult = metamapClient.getCuis(TEXT);
		List<String> cuis = new ArrayList<String>();
		cuis.add("C0012147");
		ssiImpl.getSsiScore(cuis, "C0024530");
	}

}
