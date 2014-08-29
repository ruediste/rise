package laf.component.web.defaultConfiguration;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.core.DelegatingRequestHandler;
import laf.component.core.RequestHandler;
import laf.component.core.reqestProcessing.*;
import laf.component.web.RequestMappingUtilInitializer;
import laf.component.web.requestProcessing.*;
import laf.core.base.configuration.ConfigurationDefiner;
import laf.core.base.configuration.ExtendConfiguration;
import laf.core.defaultConfiguration.*;

public class ComponentWebDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	public void produce(RequestMapperCP val,
			ControllerNameMappingCP nameMappingCV) {
		RequestMapperImpl mapper = instance.select(RequestMapperImpl.class)
				.get();
		mapper.initialize(nameMappingCV.get());
		val.set(mapper);
	}

	public void produce(InitialRequestParserCP val,
			RequestMapperCP requestMapperCV,
			ArgumentSerializerChainCP serializerChainCV,
			RequestMappingUtilInitializerCP requestMappingUtilInitializerCV) {
		InvokeInitialHandler invoker = instance.select(
				InvokeInitialHandler.class).get();
		RenderInitialPageHandler renderer = instance.select(
				RenderInitialPageHandler.class).get();
		ArgumentLoadingRequestHandler argumentLoader = instance.select(
				ArgumentLoadingRequestHandler.class).get();
		argumentLoader.initialize(serializerChainCV.get());
		PersistenceInitialRequestHandler persistence = instance.select(
				PersistenceInitialRequestHandler.class).get();
		EnterNewPageScopeHandler enterScopeHandler = instance.select(
				EnterNewPageScopeHandler.class).get();
		renderer.setDelegate(invoker);
		argumentLoader.setDelegate(renderer);
		persistence.setDelegate(argumentLoader);
		enterScopeHandler.setDelegate(persistence);

		ComponentWebInitialRequestParser parser = instance.select(
				ComponentWebInitialRequestParser.class).get();
		parser.initialize(requestMapperCV.get(), enterScopeHandler,
				requestMappingUtilInitializerCV.get());
		val.set(parser);
	}

	public void produce(ComponentActionPersistenceHandlerCP val) {
		val.set(instance.select(ComponentActionPersistenceHandler.class).get());
	}

	public void produce(ComponentActionInvokerCP val) {
		val.set(instance.select(InvokeComponentActionHandler.class).get());
	}

	public void produce(ActionRequestParserCP val, ActionPrefixCP prefix,
			ComponentActionPersistenceHandlerCP persistenceHandlerCV,
			ComponentActionInvokerCP invokerCP,
			RequestMappingUtilInitializerCP requestMappingUtilInitializerCV) {
		RequestHandler<ComponentActionRequest> invoker = invokerCP.get();
		DelegatingRequestHandler<ComponentActionRequest, ComponentActionRequest> persistenceHandler = persistenceHandlerCV
				.get();

		persistenceHandler.setDelegate(invoker);
		ComponentWebComponentActionRequestParser parser = instance.select(
				ComponentWebComponentActionRequestParser.class).get();
		parser.initialize(prefix.get(), persistenceHandler,
				requestMappingUtilInitializerCV.get());
		val.set(parser);
	}

	public void produce(ReloadPersistenceHandlerCP val) {
		val.set(instance.select(PageReloadPersistenceHandler.class).get());
	}

	public void produce(ReloadInvokerCP val) {
		val.set(instance.select(InvokeReloadHandler.class).get());
	}

	public void produce(RequestMappingUtilInitializerCP val,
			RequestMapperCP requestMapperCV, ArgumentSerializerChainCP chainCV) {
		RequestMappingUtilInitializer initializer = instance.select(
				RequestMappingUtilInitializer.class).get();
		initializer.initialize(requestMapperCV.get(), chainCV.get());
		val.set(initializer);
	}

	public void produce(ReloadRequestParserCP val, ReloadPrefixCP prefix,
			ReloadPersistenceHandlerCP persistenceCV,
			ReloadInvokerCP invokerCV,
			RequestMappingUtilInitializerCP requestMappingUtilInitializerCV) {

		RequestHandler<PageReloadRequest> invoker = invokerCV.get();
		DelegatingRequestHandler<PageReloadRequest, PageReloadRequest> persistence = persistenceCV
				.get();
		persistence.setDelegate(invoker);

		ComponentWebReloadRequestParser parser = instance.select(
				ComponentWebReloadRequestParser.class).get();
		parser.initialize(prefix.get(), persistence,
				requestMappingUtilInitializerCV.get());
		val.set(parser);
	}

	@ExtendConfiguration
	public void produce(HttpRequestParserChainCP val,
			InitialRequestParserCP requestParserCV,
			ActionRequestParserCP actionRequestParserCV,
			ReloadRequestParserCP reloadRequestParserCV) {
		val.get().parsers.add(reloadRequestParserCV.get());
		val.get().parsers.add(actionRequestParserCV.get());
		val.get().parsers.add(requestParserCV.get());
	}
}
