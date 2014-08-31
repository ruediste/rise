package laf.component.web.defaultConfiguration;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.core.DelegatingRequestHandler;
import laf.component.core.reqestProcessing.*;
import laf.component.web.RequestMappingUtilInitializer;
import laf.component.web.requestProcessing.*;
import laf.core.base.configuration.ConfigurationDefiner;
import laf.core.base.configuration.ExtendConfiguration;
import laf.core.defaultConfiguration.*;

public class ComponentWebDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	private <T> T instance(Class<T> cls) {
		return instance.select(cls).get();
	}

	public void produce(RequestMapperCP requestMapper,
			ControllerNameMappingCP nameMappingCV) {
		RequestMapperImpl mapper = instance.select(RequestMapperImpl.class)
				.get();
		mapper.initialize(nameMappingCV.get());
		requestMapper.set(mapper);
	}

	public void produce(InitialRequestParserCP val,
			RequestMapperCP requestMapperCV,
			ArgumentSerializerChainCP serializerChainCV,
			RequestMappingUtilInitializerCP requestMappingUtilInitializerCV) {

		EnterNewPageScopeHandler enterScopeHandler = instance(EnterNewPageScopeHandler.class);

		enterScopeHandler
				.setDelegate(instance(PersistenceInitialRequestHandler.class))
				.setDelegate(
						instance(ArgumentLoadingRequestHandler.class)
								.initialize(serializerChainCV.get()))
				.setDelegate(instance(RenderInitialPageHandler.class))
				.setDelegate(instance(InvokeInitialHandler.class));

		val.set(instance(ComponentWebInitialRequestParser.class).initialize(
				requestMapperCV.get(), enterScopeHandler,
				requestMappingUtilInitializerCV.get()));
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
		DelegatingRequestHandler<ComponentActionRequest, ComponentActionRequest> persistenceHandler = persistenceHandlerCV
				.get();

		persistenceHandler.setDelegate(invokerCP.get());

		val.set(instance(ComponentWebComponentActionRequestParser.class)
				.initialize(prefix.get(), persistenceHandler,
						requestMappingUtilInitializerCV.get()));
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

		DelegatingRequestHandler<PageReloadRequest, PageReloadRequest> persistence = persistenceCV
				.get();
		persistence.setDelegate(invokerCV.get());

		val.set(instance(ComponentWebReloadRequestParser.class).initialize(
				prefix.get(), persistence,
				requestMappingUtilInitializerCV.get()));
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
