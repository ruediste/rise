package laf.urlMapping.parameterHandler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationModule;
import laf.configuration.ConfigurationParameter;
import laf.controllerInfo.ControllerInfoModule;
import laf.initialization.InitializationModule;

import org.jabsaw.Module;

@Module(exported = { ConfigurationModule.class, ControllerInfoModule.class, }, imported = { InitializationModule.class })
@Singleton
public class ParameterHandlerModule {

	public final ConfigurationParameter<List<ParameterHandler>> parameterHandlers = new ConfigurationParameter<List<ParameterHandler>>(
			new ArrayList<ParameterHandler>());

	@Inject
	IntegerParameterHandler integerParameterHandler;

	@PostConstruct
	public void initialize() {
		parameterHandlers.getValue().add(integerParameterHandler);
	}

}
