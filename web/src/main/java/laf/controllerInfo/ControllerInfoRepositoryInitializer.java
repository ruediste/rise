package laf.controllerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationValue;
import laf.controllerInfo.ControllerDiscoverer.ControllerInfocCollector;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

@Singleton
public class ControllerInfoRepositoryInitializer {

	@Inject
	Logger log;

	@Inject
	ConfigurationValue<ControllerDiscoverers> discoverers;

	public void initialize(ControllerInfoRepository repository) {
		final List<Function<Predicate<Class<?>>, ControllerInfo>> controllerInfoSuppliers = new ArrayList<>();
		final Map<Class<?>, Function<Predicate<Class<?>>, ControllerInfo>> embeddedControllers = new HashMap<>();

		ControllerInfocCollector collector = new ControllerInfocCollector() {

			@Override
			public void addControllerInfo(
					Function<Predicate<Class<?>>, ControllerInfo> supplier) {
				controllerInfoSuppliers.add(supplier);
			}

			@Override
			public void addEmbeddedControllerInfo(Class<?> clazz,
					Function<Predicate<Class<?>>, ControllerInfo> supplier) {
				embeddedControllers.put(clazz, supplier);
			}

		};

		for (ControllerDiscoverer d : discoverers.value().get()) {
			d.discoverControllers(collector);
		}
		Predicate<Class<?>> isEmbeddedController = new Predicate<Class<?>>() {

			@Override
			public boolean apply(Class<?> input) {
				return embeddedControllers.containsKey(input);
			}
		};

		for (Function<Predicate<Class<?>>, ControllerInfo> sup : controllerInfoSuppliers) {
			repository.putControllerInfo(sup.apply(isEmbeddedController));
		}

		for (Function<Predicate<Class<?>>, ControllerInfo> sup : embeddedControllers
				.values()) {
			repository.putControllerInfo(sup.apply(isEmbeddedController));
		}

	}
}
