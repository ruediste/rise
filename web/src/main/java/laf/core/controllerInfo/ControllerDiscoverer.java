package laf.core.controllerInfo;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Callback to discover {@link ControllerInfo}s. Registered with {@link ControllerDiscoverersCP}.
 * 
 * @see ControllerInfoRepository
 */
public interface ControllerDiscoverer {

	/**
	 * Used by {@link ControllerDiscoverer#discoverControllers(ControllerInfoCollector)} 
	 * to register {@link ControllerInfo} suppliers.
	 */
	public interface ControllerInfoCollector {
		void addControllerInfo(
				Function<Predicate<Class<?>>, ControllerInfo> supplier);

		void addEmbeddedControllerInfo(Class<?> clazz,
				Function<Predicate<Class<?>>, ControllerInfo> supplier);
	}

	/**
	 * Register {@link ControllerInfo} suppliers with the given {@link ControllerInfoCollector}.
	 * Invoked during the initialization of the {@link ControllerInfoRepository}
	 */
	void discoverControllers(ControllerInfoCollector collector);
}
