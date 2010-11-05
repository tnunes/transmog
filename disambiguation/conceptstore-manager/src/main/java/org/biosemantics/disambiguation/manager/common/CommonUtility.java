package org.biosemantics.disambiguation.manager.common;

import java.util.Collection;

import org.biosemantics.conceptstore.common.domain.Label;
import org.biosemantics.conceptstore.common.domain.Language;
import org.springframework.context.i18n.LocaleContextHolder;

public class CommonUtility {

	public static Label getPreferredLabel(Collection<Label> preferredLabels) {
		Language localeLanguage = Language.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
		Label preferredLabel = null;
		for (Label label : preferredLabels) {
			if (label.getLanguage() == localeLanguage) {
				preferredLabel = label;
				break;
			} else if (label.getLanguage() == CommonConstants.defaultLanguage) {
				preferredLabel = label;
			} else {
				preferredLabel = label;
			}
		}
		return preferredLabel;
	}

}
