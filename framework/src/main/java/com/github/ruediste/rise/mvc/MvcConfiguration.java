package com.github.ruediste.rise.mvc;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.RequestMapper;
import com.github.ruediste.rise.core.security.web.WebRequestAuthenticator;
import com.github.ruediste.rise.core.web.ActionResultRenderer;

@Singleton
public class MvcConfiguration {

    public Supplier<RequestMapper> mapperSupplier;

    private RequestMapper mapper;

    public RequestMapper mapper() {
        return mapper;
    }

    private ChainedRequestHandler handler;

    @Inject
    private CoreConfiguration coreConfiguration;

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

    @PostConstruct
    void setupActionInvocationToPathInfoMappingFunction() {
        coreConfiguration.requestMapperProviders.add(controllerClass -> {
            if (IControllerMvc.class.isAssignableFrom(controllerClass)) {
                return Optional.of(mapper);
            } else
                return Optional.empty();
        });
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
         * Handler rendering {@link CoreRequestInfo#getActionResult()} the the
         * {@link HttpServletResponse}
         */
        public Supplier<ChainedRequestHandler> actionResultRendererSupplier;

        public Supplier<ChainedRequestHandler> authenticator;

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
    public void postConstruct(Provider<WebRequestAuthenticator> authenticator, Provider<MvcRequestMapperImpl> mapper,
            Provider<MvcControllerInvoker> invoker, Provider<ActionResultRenderer> actionResultRenderer,
            Provider<MvcPersistenceHandler> persistenceHandler) {
        supplierRefs.mapperSupplier = mapper::get;
        this.mapperSupplier = supplierRefs.mapperSupplier;

        supplierRefs.actionResultRendererSupplier = actionResultRenderer::get;
        handlerSuppliers.add(supplierRefs.actionResultRendererSupplier);

        supplierRefs.authenticator = authenticator::get;
        handlerSuppliers.add(supplierRefs.authenticator);

        supplierRefs.persistenceHandlerSupplier = persistenceHandler::get;
        handlerSuppliers.add(supplierRefs.persistenceHandlerSupplier);

        supplierRefs.controllerInvokerSupplier = invoker::get;
        finalHandlerSupplier = supplierRefs.controllerInvokerSupplier;
    }
}
