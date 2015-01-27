package com.github.ruediste.laf.core.defaultConfiguration;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializer;
import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializerChain;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.*;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.idSerializers.IntIdSerializer;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.idSerializers.LongIdSerializer;
import com.github.ruediste.laf.core.base.DefaultClassNameMapping;
import com.github.ruediste.laf.core.base.ProjectStage;
import com.github.ruediste.laf.core.base.configuration.ConfigurationDefiner;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.requestParserChain.RequestParserChain;
import com.github.ruediste.laf.core.web.resource.*;

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

	public void produce(IdSerializersCP val) {
		LinkedList<IdentifierSerializer> serializers = new LinkedList<>();
		serializers.add(get(IntIdSerializer.class));
		serializers.add(get(LongIdSerializer.class));
		val.set(serializers);
	}

	public void produce(ArgumentSerializerChainCP val,
			IdSerializersCP idSerializers) {
		ArgumentSerializerChain chain = get(ArgumentSerializerChain.class);
		EntitySerializer entitySerializer = get(EntitySerializer.class);
		entitySerializer.initialize(idSerializers.get());
		chain.initialize(Arrays.<ArgumentSerializer> asList(
				get(IntSerializer.class), get(LongSerializer.class),
				entitySerializer));
		val.set(chain);
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
		StaticWebResourceRequestHandler handler = get(StaticWebResourceRequestHandler.class);

		handler.initialize(
				projectStage.get() == ProjectStage.DEVELOPMENT ? ResourceMode.DEVELOPMENT
						: ResourceMode.PRODUCTION,
				StreamSupport.stream(
						instance.select(StaticWebResourceBundle.class).spliterator(),
						false).collect(Collectors.toList()));

		val.set(handler);
	}
}
