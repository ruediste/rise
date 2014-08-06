package laf.controllerInfo;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.base.configuration.ConfigurationValue;
import laf.controllerInfo.ControllerDiscoverer.ControllerInfoCollector;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Repository containing all controller infos.
 * 
 * <p>
 * <strong> Controller Info Creation </strong> <br/>
 * Creating {@link ControllerInfo}s is a fully customizable, two stage process.
 * The two stages are required since creating {@link ControllerInfo}s depends on
 * knowing the embedded controllers. In the first stage, all controllers are
 * identified and categorized to normal controllers and embedded controllers. In
 * the second stage, the actual {@link ControllerInfo}s are created. To
 * participate in the discovery process, a {@link ControllerDiscoverer} has to
 * be added to the {@link ControllerDiscoverersCP} configuration parameter.
 * </p>
 * 
 * <p>
 * When the {@link ControllerInfoRepository} is initialized, it invokes
 * {@link ControllerDiscoverer#discoverControllers(ControllerInfoCollector)} on
 * all registered discoverers. It is the responsibility of the discoverers to
 * register controller info suppliers with the supplied
 * {@link ControllerInfoCollector}. In the second stage, all suppliers are
 * invoked and the results added to the repository. To create
 * {@link ControllerInfo}s based on reflection, use
 * {@link ControllerInfoCreationService#createControllerInfo(Class, Object, Predicate, laf.controllerInfo.ControllerInfoCreationService.ControllerInfoCustomizer)}
 * </p>
 */
@ApplicationScoped
public class ControllerInfoRepository {
	@Inject
	ConfigurationValue<ControllerDiscoverersCP> discoverers;

	private final LinkedHashMap<Class<?>, ControllerInfo> controllerInfos = new LinkedHashMap<>();

	public void putControllerInfo(ControllerInfo info) {
		controllerInfos.put(info.getControllerClass(), info);
	}

	public Iterable<ControllerInfo> getControllerInfos() {
		return controllerInfos.values();
	}

	public ControllerInfo getControllerInfo(Class<?> controllerClass) {
		return controllerInfos.get(controllerClass);
	}

	@PostConstruct
	void initialize() {
		final List<Function<Predicate<Class<?>>, ControllerInfo>> controllerInfoSuppliers = new ArrayList<>();
		final Map<Class<?>, Function<Predicate<Class<?>>, ControllerInfo>> embeddedControllers = new HashMap<>();

		// create collector
		ControllerInfoCollector collector = new ControllerInfoCollector() {

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

		// invoke discoverers
		for (ControllerDiscoverer d : discoverers.value().get()) {
			d.discoverControllers(collector);
		}

		// create embedded controller predicate
		Predicate<Class<?>> isEmbeddedController = new Predicate<Class<?>>() {

			@Override
			public boolean apply(Class<?> input) {
				return embeddedControllers.containsKey(input);
			}
		};

		// invoke all suppliers
		for (Function<Predicate<Class<?>>, ControllerInfo> sup : controllerInfoSuppliers) {
			putControllerInfo(sup.apply(isEmbeddedController));
		}

		for (Function<Predicate<Class<?>>, ControllerInfo> sup : embeddedControllers
				.values()) {
			putControllerInfo(sup.apply(isEmbeddedController));
		}
	}
}
