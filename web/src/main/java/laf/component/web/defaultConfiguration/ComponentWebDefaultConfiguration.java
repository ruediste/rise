package laf.component.web.defaultConfiguration;

import java.util.ArrayDeque;
import java.util.Arrays;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.core.*;
import laf.component.web.*;
import laf.component.web.basic.template.CLinkHtmlTemplate;
import laf.core.base.configuration.ConfigurationDefiner;
import laf.core.base.configuration.ExtendConfiguration;
import laf.core.defaultConfiguration.*;

public class ComponentWebDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	private <T> T instance(Class<T> cls) {
		return instance.select(cls).get();
	}

	public void produce(HtmlTemplateFactoriesCP val) {
		ArrayDeque<HtmlTemplateFactory> queue = new ArrayDeque<>();
		HtmlTemplateFactoryImpl factory = instance(HtmlTemplateFactoryImpl.class);
		queue.add(factory);
		factory.addTemplatesFromPackage(CLinkHtmlTemplate.class.getPackage());
		val.set(queue);
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
			UtilInitializersCP utilInitializersCV) {

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
				utilInitializersCV.get()));
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
			UtilInitializersCP utilInitializersCV) {
		DelegatingRequestHandler<ComponentActionRequest, ComponentActionRequest> persistenceHandler = persistenceHandlerCV
				.get();

		persistenceHandler.setDelegate(invokerCP.get());

		val.set(instance(ComponentWebComponentActionRequestParser.class)
				.initialize(prefix.get(), persistenceHandler,
						utilInitializersCV.get()));
	}

	public void produce(ReloadPersistenceHandlerCP val) {
		val.set(instance.select(PageReloadPersistenceHandler.class).get());
	}

	public void produce(ReloadInvokerCP val) {
		val.set(instance.select(InvokeReloadHandler.class).get());
	}

	public void produce(UtilInitializersCP val,
			RequestMapperCP requestMapperCV, ArgumentSerializerChainCP chainCV,
			HtmlTemplateFactoriesCP htmlTemplateFactoriesCV) {

		RequestMappingUtilInitializer requestMappingUtilInitializer = instance(RequestMappingUtilInitializer.class);
		requestMappingUtilInitializer.initialize(requestMapperCV.get(),
				chainCV.get());

		TemplateUtilInitializer templateUtilInitializer = instance(TemplateUtilInitializer.class);
		templateUtilInitializer.initialize(htmlTemplateFactoriesCV.get());

		val.set(Arrays.asList(requestMappingUtilInitializer,
				templateUtilInitializer));
	}

	public void produce(ReloadRequestParserCP val, ReloadPrefixCP prefix,
			ReloadPersistenceHandlerCP persistenceCV,
			ReloadInvokerCP invokerCV, UtilInitializersCP utilInitializers) {

		DelegatingRequestHandler<PageReloadRequest, PageReloadRequest> persistence = persistenceCV
				.get();
		persistence.setDelegate(invokerCV.get());

		val.set(instance(ComponentWebReloadRequestParser.class).initialize(
				prefix.get(), persistence, utilInitializers.get()));
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
