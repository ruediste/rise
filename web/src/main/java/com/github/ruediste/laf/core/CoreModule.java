package com.github.ruediste.laf.core;

import java.lang.reflect.Field;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.argumentSerializer.CoreArgumentSerializerModule;
import com.github.ruediste.laf.core.base.CoreBaseModule;
import com.github.ruediste.laf.core.base.configuration.*;
import com.github.ruediste.laf.core.base.configuration.ConfigurationFactory.NoValueFoundException;
import com.github.ruediste.laf.core.http.CoreHttpModule;
import com.github.ruediste.laf.core.persistence.PersistenceModule;
import com.github.ruediste.laf.core.requestParserChain.CoreRequestParserChainModule;
import com.github.ruediste.laf.core.web.resource.StaticWebResourceModule;
import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

@Module(exported = { CoreArgumentSerializerModule.class, CoreBaseModule.class,
		PersistenceModule.class, CoreRequestParserChainModule.class,
		CoreHttpModule.class, StaticWebResourceModule.class })
public class CoreModule extends AbstractModule {

	@Override
	protected void configure() {
		Provider<ConfigurationFactory> factoryProvider = getProvider(ConfigurationFactory.class);

		// bind configuration parameters
		bindListener(Matchers.any(), new TypeListener() {

			@Override
			public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
				TypeLiteral<?> t = type;

				while (!Object.class.equals(t.getRawType())) {
					for (Field field : type.getRawType().getDeclaredFields()) {
						Class<?> fieldType = type.getFieldType(field)
								.getRawType();
						if (ConfigurationParameter.class
								.isAssignableFrom(fieldType)) {
							field.setAccessible(true);
							encounter.register(new MembersInjector<I>() {

								@Override
								public void injectMembers(I instance) {
									try {
										field.set(
												instance,
												factoryProvider
														.get()
														.createParameterInstance(
																fieldType));
									} catch (IllegalArgumentException
											| IllegalAccessException e) {
										throw new RuntimeException(
												"Error while injecting configuration parameter",
												e);
									} catch (NoValueFoundException e) {
										throw new RuntimeException(
												"Error while retrieving configuration value for "
														+ fieldType
														+ ".\nRequired for member"
														+ field.getDeclaringClass()
														+ "." + field.getName());
									}
								}
							});
						}
					}
					t = t.getSupertype(t.getRawType().getSuperclass());
				}
			}
		});
	}
}
