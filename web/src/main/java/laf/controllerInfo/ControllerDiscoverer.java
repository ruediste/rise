package laf.controllerInfo;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public interface ControllerDiscoverer {

	interface ControllerInfocCollector {
		void addControllerInfo(
				Function<Predicate<Class<?>>, ControllerInfo> supplier);

		void addEmbeddedControllerInfo(Class<?> clazz,
				Function<Predicate<Class<?>>, ControllerInfo> supplier);
	}

	void discoverControllers(ControllerInfocCollector collector);
}
