package laf.core.defaultConfiguration;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.core.base.DefaultClassNameMapping;
import laf.core.base.ProjectStage;
import laf.core.base.configuration.ConfigurationDefiner;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParserChain;
import laf.core.web.resource.ResourceRequestHandler;
import laf.core.web.resource.ResourceType;
import ro.isdc.wro.extensions.processor.css.RubySassCssProcessor;
import ro.isdc.wro.extensions.processor.css.YUICssCompressorProcessor;
import ro.isdc.wro.extensions.processor.js.UglifyJsProcessor;

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
		val.set(ProjectStage.DEVELOPMENT);
	}

	public void produce(ResourceRequestHandlerCP val,
			ProjectStageCP projectStage) {
		ResourceRequestHandler handler = get(ResourceRequestHandler.class);
		handler.initialize("static/", "static/",
				projectStage.get() != ProjectStage.DEVELOPMENT);

		handler.getResourceHandlers().put(ResourceType.SASS, (in, out) -> {
			try {
				new RubySassCssProcessor().process(in, out);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		if (projectStage.get() == ProjectStage.DEVELOPMENT) {
		} else {

			handler.getConcatenatedResourceHandlers().put(ResourceType.CSS,
					(in, out) -> {
						try {
							new YUICssCompressorProcessor().process(in, out);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});

			handler.getConcatenatedResourceHandlers().put(ResourceType.JS,
					(in, out) -> {
						try {
							new UglifyJsProcessor().process(in, out);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
		}
		val.set(handler);
	}
}
