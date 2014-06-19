package laf.mvc.configuration;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.configuration.ConfigurationDefiner;
import laf.configuration.ExtendConfiguration;
import laf.mvc.*;
import laf.requestProcessing.ControllerInvokerMap;

public class MvcDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	@ExtendConfiguration
	public void produce(ControllerInvokerMap map) {
		MvcPersistenceControllerInvoker persistenceInvoker = instance.select(
				MvcPersistenceControllerInvoker.class).get();
		persistenceInvoker.setDelegate(instance.select(
				MvcControllerInvoker.class).get());
		map.get().put(Controller.class, persistenceInvoker);
	}

}
