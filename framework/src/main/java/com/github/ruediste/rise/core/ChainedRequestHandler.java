package com.github.ruediste.rise.core;

import java.util.LinkedList;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.rise.core.web.ActionResultRenderer;
import com.github.ruediste.rise.mvc.MvcConfiguration;
import com.github.ruediste.rise.mvc.MvcControllerInvoker;
import com.github.ruediste.rise.mvc.MvcPersistenceHandler;
import com.github.ruediste.rise.mvc.MvcRequestMapperImpl;
import com.github.ruediste.rise.util.ChainedRunnable;

public abstract class ChainedRequestHandler extends ChainedRunnable {
    public Supplier<RequestMapper> mapperSupplier;

    private RequestMapper mapper;

    RequestMapper mapper() {
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
         * {@link MvcConfiguration#mapperSupplier}
         */
        public Supplier<RequestMapper> mapperSupplier;

        /**
         * Handler rendering {@link CoreRequestInfo#getActionResult()} to the
         * {@link HttpServletResponse}
         */
        public Supplier<ChainedRequestHandler> actionResultRendererSupplier;

        /**
         * Handler managing transactions and persistence context.
         */
        public Supplier<ChainedRequestHandler> persistenceHandlerSupplier;

        /**
         * Instantiates the controller and invokes the action method. By default
         * registered as the {@link MvcConfiguration#finalHandlerSupplier}
         */
        public Supplier<Runnable> controllerInvokerSupplier;

    }

    public final SupplierRefs supplierRefs = new SupplierRefs();

    @PostConstruct
    public void postConstruct(Provider<MvcRequestMapperImpl> mapper,
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
