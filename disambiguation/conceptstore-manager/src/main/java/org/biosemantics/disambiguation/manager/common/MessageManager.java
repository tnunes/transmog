package org.biosemantics.disambiguation.manager.common;

import java.util.Locale;

public interface MessageManager {
	String getMessage(String code, Object[] args, Locale locale);
	String getMessage(String code);
	String getMessage(String code, Object[] args);
}
