package com.github.ruediste.laf.mvc.web;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.salta.jsr330.Injector;

@Singleton
public class MvcWebConfiguration {

	@Inject
	Injector injector;

	public Supplier<MvcWebRequestMapper> mapperSupplier = () -> injector
			.getInstance(MvcWebRequestMapperImpl.class);

	private MvcWebRequestMapper mapper;

	public MvcWebRequestMapper mapper() {
		return mapper;
	}

	public void initialize() {
		mapper = mapperSupplier.get();
		mapper.registerControllers();
	}

}
