package org.biosemantics.conceptstore.repository;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.Label;

public interface LabelRepository {

	public abstract Label create(String text, String language);

	public abstract Label getById(long id);

	public abstract Label getOrCreate(String text, String language);

	public abstract Collection<Label> getByText(String text);

}