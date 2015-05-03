package com.github.ruediste.laf.core.persistence.em;

import static java.util.stream.Collectors.toMap;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;

import com.github.ruediste.laf.core.persistence.DataBaseLinkRegistry;

@Singleton
public class PersisteUnitRegistry {

	@Inject
	DataBaseLinkRegistry registry;

	private ConcurrentHashMap<Class<? extends Annotation>, Optional<EntityManagerFactory>> factories = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class<? extends Annotation>, Optional<Map<Class<?>, ManagedType<?>>>> managedTypeMaps = new ConcurrentHashMap<>();

	private static @interface NullQualifier {
	}

	public Optional<EntityManagerFactory> getUnit(
			Class<? extends Annotation> qualifier) {
		return factories.computeIfAbsent(
				qualifier == null ? NullQualifier.class : qualifier,
				q -> Optional.ofNullable(registry.getLink(qualifier)).map(
						link -> link.createEntityManagerFactory()));

	}

	public Optional<Map<Class<?>, ManagedType<?>>> getManagedTypeMap(
			Class<? extends Annotation> qualifier) {
		return managedTypeMaps
				.computeIfAbsent(
						qualifier == null ? NullQualifier.class : qualifier,
						q -> getUnit(qualifier)
								.map(new Function<EntityManagerFactory, Map<Class<?>, ManagedType<?>>>() {
									@Override
									public Map<Class<?>, ManagedType<?>> apply(
											EntityManagerFactory unit) {
										return unit
												.getMetamodel()
												.getManagedTypes()
												.stream()
												.collect(
														toMap(t -> t
																.getJavaType(),
																t -> t));
									}
								}));

	}

	public void closeAll() {
		factories.values().stream()
				.forEach(o -> o.ifPresent(emf -> emf.close()));
	}
}
