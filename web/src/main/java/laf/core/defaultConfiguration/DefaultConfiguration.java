package laf.core.defaultConfiguration;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.core.base.DefaultClassNameMapping;
import laf.core.base.configuration.ConfigurationDefiner;
import laf.core.http.request.HttpRequest;
import laf.core.requestParserChain.RequestParserChain;

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

	public void produce(HttpRequestParserChainCP val) {
		val.set(instance.select(
				new TypeLiteral<RequestParserChain<HttpRequest>>() {
					private static final long serialVersionUID = 1L;
				}).get());
	}
}
