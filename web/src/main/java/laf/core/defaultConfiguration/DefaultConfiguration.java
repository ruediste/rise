package laf.core.defaultConfiguration;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

	public void produce(ResourceModeCP val, ProjectStageCP projectStage) {
		if (projectStage.get() == ProjectStage.DEVELOPMENT) {
			val.set(ResourceMode.DEVELOPMENT);
		} else {
			val.set(ResourceMode.PRODUCTION);
		}
	}

	public void produce(ResourceRequestHandlerCP val,
			ProjectStageCP projectStage) {
		ResourceRequestHandler handler = get(ResourceRequestHandler.class);

		handler.initialize(
				projectStage.get() == ProjectStage.DEVELOPMENT ? ResourceMode.DEVELOPMENT
						: ResourceMode.PRODUCTION,
				StreamSupport.stream(
						instance.select(ResourceBundle.class).spliterator(),
						false).collect(Collectors.toList()));

		val.set(handler);
	}
}
