package com.github.ruediste.laf.mvc.web;

import java.util.LinkedList;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.laf.core.CoreRequestInfo;
import com.github.ruediste.laf.core.RequestMapper;
import com.github.ruediste.laf.core.web.ActionResultRenderer;

@Singleton
public class MvcWebConfiguration {

	public Supplier<RequestMapper> mapperSupplier;

	private RequestMapper mapper;

	public RequestMapper mapper() {
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

	/**
	 * Supplier for the final request handler. Initialized with
	 * {@link SupplierRefs#controllerInvokerSupplier}
	 */
	public Supplier<Runnable> finalHandlerSupplier;

	public void handleRequest() {
		handler.run();
	}

	public static class SupplierRefs {
		/**
		 * Supplier for the mapper. By default registered as
		 * {@link MvcWebConfiguration#mapperSupplier}
		 */
		public Supplier<RequestMapper> mapperSupplier;

		/**
		 * Handler rendering {@link CoreRequestInfo#getActionResult()} the the
		 * {@link HttpServletResponse}
		 */
		public Supplier<ChainedRequestHandler> actionResultRendererSupplier;

		/**
		 * Handler managing transactions and persistence context.
		 */
		public Supplier<ChainedRequestHandler> persistenceHandlerSupplier;

		/**
		 * Instantiates the controller and invokes the action method. By default
		 * registered as the {@link MvcWebConfiguration#finalHandlerSupplier}
		 */
		public Supplier<Runnable> controllerInvokerSupplier;

	}

	public final SupplierRefs supplierRefs = new SupplierRefs();

	@PostConstruct
	public void postConstruct(Provider<MvcWebRequestMapperImpl> mapper,
			Provider<MvcControllerInvoker> invoker,
			Provider<ActionResultRenderer> actionResultRenderer,
			Provider<MvcPersistenceHandler> persistenceHandler) {
		supplierRefs.mapperSupplier = mapper::get;
		this.mapperSupplier = supplierRefs.mapperSupplier;

		supplierRefs.actionResultRendererSupplier = actionResultRenderer::get;
		handlerSuppliers.add(supplierRefs.actionResultRendererSupplier);

		supplierRefs.persistenceHandlerSupplier = persistenceHandler::get;
		handlerSuppliers.add(supplierRefs.persistenceHandlerSupplier);

		supplierRefs.controllerInvokerSupplier = invoker::get;
		finalHandlerSupplier = supplierRefs.controllerInvokerSupplier;
	}
}
