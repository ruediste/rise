package com.github.ruediste.rise.crud;

import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

@Singleton
public class CrudModelReader {

	ConcurrentHashMap<Class<?>, CrudModel> cache = new ConcurrentHashMap<>();

	public CrudModel getCrudModel(Class<?> clazz) {
		return cache.computeIfAbsent(clazz, c -> createCrudModel(clazz));
	}

	private CrudModel createCrudModel(Class<?> clazz) {
		return new CrudModel();
	}
}
