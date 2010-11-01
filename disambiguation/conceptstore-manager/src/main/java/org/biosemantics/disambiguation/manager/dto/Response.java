package org.biosemantics.disambiguation.manager.dto;

import java.io.Serializable;

public interface Response extends Serializable {
	ResponseType getResponseType();

	String getMessage();

	int getResultSize();

	long getResponseTime();
}
