package laf.core.defaultConfiguration;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.core.base.DefaultClassNameMapping;
import laf.core.base.ProjectStage;
import laf.core.base.configuration.ConfigurationDefiner;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParserChain;
import laf.core.web.resource.*;
import ro.isdc.wro.extensions.processor.css.RubySassCssProcessor;
import ro.isdc.wro.model.resource.processor.impl.css.JawrCssMinifierProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

/**
 * Defines the default configuration of the framework.
 */
public class DefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	private <T> T get(Class<T> cls) {
		return instance.select(cls).get();
	}

	public void produce(ControllerNameMappingCP val, BasePackageCP basePackage) {
		DefaultClassNameMapping mapping = get(DefaultClassNameMapping.class);
		mapping.initialize(basePackage.get(), "Controller");
		val.set(mapping);
	}

	public void produce(ArgumentSerializerChainCP val) {
		val.set(get(ArgumentSerializerChain.class));
	}

	public void produce(HttpRequestParserChainCP val,
			ResourceRequestHandlerCP resourceRequestHandlerCV) {
		RequestParserChain<HttpRequest> chain = instance.select(
				new TypeLiteral<RequestParserChain<HttpRequest>>() {
					private static final long serialVersionUID = 1L;
				}).get();

		chain.add(resourceRequestHandlerCV.get());
		val.set(chain);
	}

	public void produce(ProjectStageCP val) {
		val.set(ProjectStage.TESTING);
	}

	public void produce(ResourceRequestHandlerCP val,
			ProjectStageCP projectStage) {
		ResourceRequestHandler handler;

		if (projectStage.get() == ProjectStage.DEVELOPMENT) {
			IndividualResourceRequestHandler individualHandler = get(IndividualResourceRequestHandler.class);
			individualHandler.initialize("static/", "static/");
			handler = individualHandler;
		} else {

			BundleResourceRequestHandler bundleHandler = get(BundleResourceRequestHandler.class);
			bundleHandler.initialize("static/", "static/");
			handler = bundleHandler;

			bundleHandler.addBundleTransformer(ResourceType.CSS,
					(in, out) -> new JawrCssMinifierProcessor()
							.process(in, out));

			bundleHandler.addBundleTransformer(ResourceType.JS,
					(in, out) -> new JSMinProcessor().process(in, out));
		}

		handler.addResourceTransformer(ResourceType.SASS, ResourceType.CSS, (
				in, out) -> new RubySassCssProcessor().process(in, out));

		val.set(handler);
	}
}
