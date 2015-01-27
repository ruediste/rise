package com.github.ruediste.laf.component.web.defaultConfiguration;

import java.util.ArrayDeque;
import java.util.Arrays;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.github.ruediste.laf.component.core.*;
import com.github.ruediste.laf.component.web.*;
import com.github.ruediste.laf.component.web.components.template.CLinkHtmlTemplate;
import com.github.ruediste.laf.core.base.configuration.ConfigurationDefiner;
import com.github.ruediste.laf.core.base.configuration.ExtendConfiguration;
import com.github.ruediste.laf.core.defaultConfiguration.*;

public class ComponentWebDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	private <T> T get(Class<T> cls) {
		return instance.select(cls).get();
	}

	public void produce(HtmlTemplateFactoriesCP val) {
		ArrayDeque<HtmlTemplateFactory> queue = new ArrayDeque<>();
		HtmlTemplateFactoryImpl factory = get(HtmlTemplateFactoryImpl.class);
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

		EnterNewPageScopeHandler enterScopeHandler = get(EnterNewPageScopeHandler.class);

		enterScopeHandler
				.setDelegate(get(PersistenceInitialRequestHandler.class))
				.setDelegate(
						get(ArgumentLoadingRequestHandler.class).initialize(
								serializerChainCV.get()))
				.setDelegate(get(RenderInitialPageHandler.class))
				.setDelegate(get(InvokeInitialHandler.class));

		val.set(get(ComponentWebInitialRequestParser.class).initialize(
				requestMapperCV.get(), enterScopeHandler,
				utilInitializersCV.get()));
	}

	public void produce(ComponentActionPersistenceHandlerCP val) {
		val.set(instance.select(ComponentActionPersistenceHandler.class).get());
	}

	public void produce(ComponentActionInvokerCP val) {
		val.set(instance.select(InvokeComponentActionHandler.class).get());
	}

	public void produce(ActionRequestParserCP val, ActionPathCP prefix,
			ComponentActionPersistenceHandlerCP persistenceHandlerCV,
			ComponentActionInvokerCP invokerCP,
			UtilInitializersCP utilInitializersCV) {
		DelegatingRequestHandler<ComponentActionRequest, ComponentActionRequest> persistenceHandler = persistenceHandlerCV
				.get();

		persistenceHandler.setDelegate(invokerCP.get());

		val.set(get(ComponentWebComponentActionRequestParser.class).initialize(
				prefix.get(), persistenceHandler, utilInitializersCV.get()));
	}

	public void produce(ReloadPersistenceHandlerCP val) {
		val.set(instance.select(PageReloadPersistenceHandler.class).get());
	}

	public void produce(ReloadInvokerCP val) {
		val.set(instance.select(InvokeReloadHandler.class).get());
	}

	public void produce(UtilInitializersCP val,
			RequestMapperCP requestMapperCV, ArgumentSerializerChainCP chainCV,
			HtmlTemplateFactoriesCP htmlTemplateFactoriesCV,
			ReloadPathCP reloadPrefixCV) {

		WebRequestInfoInitializer initializer = get(WebRequestInfoInitializer.class);
		initializer.initialize(chainCV.get(), reloadPrefixCV.get(),
				requestMapperCV.get(), htmlTemplateFactoriesCV.get());

		val.set(Arrays.asList(initializer));
	}

	public void produce(ReloadRequestParserCP val, ReloadPathCP prefix,
			ReloadPersistenceHandlerCP persistenceCV,
			ReloadInvokerCP invokerCV, UtilInitializersCP utilInitializersCV) {

		DelegatingRequestHandler<PageReloadRequest, PageReloadRequest> persistence = persistenceCV
				.get();
		persistence.setDelegate(invokerCV.get());

		val.set(get(ComponentWebReloadRequestParser.class).initialize(
				prefix.get(), persistence, utilInitializersCV.get()));
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
