package org.biosemantics.conceptstore.repository;

import java.util.Collection;

import org.biosemantics.conceptstore.domain.Notation;

public interface NotationRepository {

	public abstract Notation create(String source, String code);

	public abstract Notation getById(long id);

	public abstract Collection<Notation> getByCode(String code);

	public abstract Notation getOrCreate(String source, String code);

}