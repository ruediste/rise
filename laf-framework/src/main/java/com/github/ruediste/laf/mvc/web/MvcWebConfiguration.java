package com.github.ruediste.laf.mvc.web;

import java.util.LinkedList;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class MvcWebConfiguration {

	public Supplier<MvcWebRequestMapper> mapperSupplier;

	private MvcWebRequestMapper mapper;

	MvcWebRequestMapper mapper() {
		return mapper;
	}

	private ChainedRequestHandler handler;

	public void initialize() {
		mapper = mapperSupplier.get();
		mapper.initialize();

		ChainedRequestHandler last = null;
		for (Supplier<ChainedRequestHandler> supplier : handlerSuppliers) {
			ChainedRequestHandler handler = supplier.get();
			if (this.handler == null)
				this.handler = handler;
			if (last != null)
				last.setNext(handler);
			last = handler;
		}
		if (last != null) {
			last.setNext(finalHandlerSupplier.get());
		}
	}

	public final LinkedList<Supplier<ChainedRequestHandler>> handlerSuppliers = new LinkedList<>();

	public Supplier<Runnable> finalHandlerSupplier;

	public void handleRequest() {
		handler.run();
	}

	public static class SupplierRefs {
		public Supplier<MvcWebRequestMapper> mapperSupplier;
		public Supplier<Runnable> controllerInvokerSupplier;
		public Supplier<ChainedRequestHandler> actionResultRendererSupplier;
	}

	public final SupplierRefs supplierRefs = new SupplierRefs();

	@PostConstruct
	public void postConstruct(Provider<MvcWebRequestMapperImpl> mapper,
			Provider<ControllerInvoker> invoker,
			Provider<ActionResultRenderer> actionResultRenderer) {
		supplierRefs.mapperSupplier = mapper::get;
		this.mapperSupplier = supplierRefs.mapperSupplier;

		supplierRefs.actionResultRendererSupplier = actionResultRenderer::get;
		handlerSuppliers.add(supplierRefs.actionResultRendererSupplier);

		supplierRefs.controllerInvokerSupplier = invoker::get;
		finalHandlerSupplier = supplierRefs.controllerInvokerSupplier;
	}
}
