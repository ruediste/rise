package com.github.ruediste.rise.component;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.initial.ComponentControllerInvoker;
import com.github.ruediste.rise.component.initial.ComponentRequestMapperImpl;
import com.github.ruediste.rise.component.initial.InitialPagePersistenceHandler;
import com.github.ruediste.rise.component.initial.PageCreationHandler;
import com.github.ruediste.rise.component.initial.ViewRenderer;
import com.github.ruediste.rise.component.reload.ReloadHandler;
import com.github.ruediste.rise.component.reload.ReloadPagePersistenceHandler;
import com.github.ruediste.rise.component.reload.ReloadPageScopeHandler;
import com.github.ruediste.rise.component.reload.ReloadRequestParser;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.RequestMapper;
import com.github.ruediste.rise.core.RequestParser;
import com.github.ruediste.rise.core.web.ActionResultRenderer;

@Singleton
public class ComponentConfiguration {

    public Supplier<RequestMapper> mapperSupplier;

    private RequestMapper mapper;

    public RequestMapper mapper() {
        return mapper;
    }

    @Inject
    private CoreConfiguration coreConfiguration;

    public void initialize() {
        mapper = mapperSupplier.get();
        mapper.initialize();
        viewFactory = viewFactorySupplier.get();
        coreConfiguration.actionInvocationToPathInfoMappingFunctions
                .add(invocation -> {
                    if (IControllerComponent.class
                            .isAssignableFrom(invocation.methodInvocation
                                    .getInstanceClass()))
                        return Optional.of(mapper.generate(invocation));
                    else
                        return Optional.empty();
                });

        this.initialHandler = chainHandlers(initialHandlerSuppliers,
                finalInitialHandlerSupplier);
        this.reloadHandler = chainHandlers(reloadHandlerSuppliers,
                finalReloadHandlerSupplier);
        ajaxParser = ajaxParserSupplier.get();
        reloadParser = reloadParserSupplier.get();
    }

    private ChainedRequestHandler reloadHandler;
    private ChainedRequestHandler initialHandler;
    /**
     * list of suppliers used for {@link #handleInitialRequest()}
     */
    public final LinkedList<Supplier<ChainedRequestHandler>> initialHandlerSuppliers = new LinkedList<>();
    public final LinkedList<Supplier<ChainedRequestHandler>> reloadHandlerSuppliers = new LinkedList<>();

    /**
     * Supplier for the final request handler. Initialized to
     * {@link InitialChainSupplierRefs#controllerInvokerSupplier}
     */
    public Supplier<Runnable> finalInitialHandlerSupplier;
    public Supplier<Runnable> finalReloadHandlerSupplier;

    public void handleInitialRequest() {
        initialHandler.run();
    }

    public void handleReloadRequest() {
        reloadHandler.run();
    }

    public static class InitialChainSupplierRefs {
        /**
         * Supplier for the mapper. By default registered as
         * {@link ComponentConfiguration#mapperSupplier}
         */
        public Supplier<RequestMapper> mapperSupplier;

        /**
         * Handler rendering {@link CoreRequestInfo#getActionResult()} to the
         * {@link HttpServletResponse}
         */
        public Supplier<ChainedRequestHandler> actionResultRendererSupplier;

        /**
         * Handler creating the page, including the controller and the view
         */
        public Supplier<ChainedRequestHandler> pageCreationHandler;

        public Supplier<ChainedRequestHandler> initialPagePersistenceHandler;

        public Supplier<ChainedRequestHandler> viewRenderer;

        /**
         * Instantiates the controller and invokes the action method. By default
         * registered as the
         * {@link ComponentConfiguration#finalInitialHandlerSupplier}
         */
        public Supplier<Runnable> controllerInvokerSupplier;

    }

    public final InitialChainSupplierRefs initialChain = new InitialChainSupplierRefs();

    @PostConstruct
    public void constructInitialChain(
            Provider<ComponentRequestMapperImpl> mapper,
            Provider<ComponentControllerInvoker> invoker,
            Provider<ActionResultRenderer> actionResultRenderer,
            Provider<PageCreationHandler> pageCreationHandler,
            Provider<InitialPagePersistenceHandler> initialPagePersistenceHandler,
            Provider<ViewRenderer> viewRenderer) {
        initialChain.mapperSupplier = mapper::get;
        this.mapperSupplier = initialChain.mapperSupplier;

        initialChain.actionResultRendererSupplier = actionResultRenderer::get;
        initialHandlerSuppliers.add(initialChain.actionResultRendererSupplier);

        initialChain.pageCreationHandler = pageCreationHandler::get;
        initialHandlerSuppliers.add(initialChain.pageCreationHandler);

        initialChain.initialPagePersistenceHandler = initialPagePersistenceHandler::get;
        initialHandlerSuppliers.add(initialChain.initialPagePersistenceHandler);

        initialChain.viewRenderer = viewRenderer::get;
        initialHandlerSuppliers.add(initialChain.viewRenderer);

        initialChain.controllerInvokerSupplier = invoker::get;
        finalInitialHandlerSupplier = initialChain.controllerInvokerSupplier;
    }

    @PostConstruct
    public void postConstruct(
            Provider<ComponentViewRepository> componentViewRepository) {
        viewFactorySupplier = () -> componentViewRepository.get()::createView;
    }

    public static class ReloadChainSupplierRefs {
        /**
         * Handler rendering {@link CoreRequestInfo#getActionResult()} to the
         * {@link HttpServletResponse}
         */
        public Supplier<ChainedRequestHandler> actionResultRendererSupplier;

        public Supplier<ChainedRequestHandler> pageScopeHandler;
        public Supplier<ChainedRequestHandler> persistenceHandler;

        public Supplier<Runnable> reloadHandler;
    }

    ReloadChainSupplierRefs reloadChain = new ReloadChainSupplierRefs();

    @PostConstruct
    public void constructReloadChain(
            Provider<ActionResultRenderer> actionResultRenderer,
            Provider<ReloadPageScopeHandler> scopeHandler,
            Provider<ReloadPagePersistenceHandler> persistenceHandler,
            Provider<ReloadHandler> reloadHandler,
            Provider<ReloadRequestParser> reloadParser,
            Provider<AjaxRequestParser> ajaxParser) {
        reloadChain.actionResultRendererSupplier = actionResultRenderer::get;
        reloadHandlerSuppliers.add(reloadChain.actionResultRendererSupplier);

        reloadChain.pageScopeHandler = scopeHandler::get;
        reloadHandlerSuppliers.add(reloadChain.pageScopeHandler);

        reloadChain.persistenceHandler = persistenceHandler::get;
        reloadHandlerSuppliers.add(reloadChain.persistenceHandler);

        reloadChain.reloadHandler = reloadHandler::get;
        finalReloadHandlerSupplier = reloadChain.reloadHandler;

        reloadParserSupplier = reloadParser::get;
        ajaxParserSupplier = ajaxParser::get;
    }

    public Supplier<BiFunction<IControllerComponent, Class<? extends IViewQualifier>, ViewComponentBase<?>>> viewFactorySupplier;
    public BiFunction<IControllerComponent, Class<? extends IViewQualifier>, ViewComponentBase<?>> viewFactory;

    public ViewComponentBase<?> createView(IControllerComponent controller) {
        return createView(controller, null);
    }

    public ViewComponentBase<?> createView(IControllerComponent controller,
            Class<? extends IViewQualifier> qualifier) {
        return viewFactory.apply(controller, qualifier);
    }

    private String reloadPath = "/~component/reload";

    public String getReloadPath() {
        return reloadPath;
    }

    private String ajaxPath = "/~component/ajax";

    public String getAjaxPath() {
        return ajaxPath;
    }

    private ChainedRequestHandler chainHandlers(
            LinkedList<Supplier<ChainedRequestHandler>> suppliers,
            Supplier<Runnable> finalSupplier) {
        ChainedRequestHandler result = null;
        ChainedRequestHandler last = null;
        for (Supplier<ChainedRequestHandler> supplier : suppliers) {
            ChainedRequestHandler handler = supplier.get();
            if (result == null)
                result = handler;
            if (last != null)
                last.setNext(handler);
            last = handler;
        }
        if (last != null) {
            last.setNext(finalSupplier.get());
        }
        return result;
    }

    public Supplier<RequestParser> reloadParserSupplier;
    private RequestParser reloadParser;

    public RequestParser getReloadParser() {
        return reloadParser;
    }

    public Supplier<RequestParser> ajaxParserSupplier;
    private RequestParser ajaxParser;

    public RequestParser getAjaxParser() {
        return ajaxParser;
    }

}
