package laf.httpRequestMapping.parameterHandler;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.Laf;
import laf.configuration.ConfigurationModule;
import laf.configuration.ConfigurationParameter;
import laf.configuration.LoadDefaultConfigurationEvent;
import laf.controllerInfo.ControllerInfoModule;
import laf.controllerInfo.ParameterInfo;
import laf.httpRequestMapping.HttpRequestMappingRule;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProviderModule;
import laf.initialization.InitializationModule;

import org.jabsaw.Module;

/**
 * <p>
 * In many cases the mapping of URLs can be split in two aspects: There is the
 * overall format of the URL and the way individual parameters are represented.
 * While the configuration of the mapping rules gives you complete freedom of
 * the mapping, we implemented an infrastructure for parameter value handling.
 * This infrastructure is used by the default {@link HttpRequestMappingRule}s, but can
 * as well be used by your own customizations.
 * </p>
 *
 * <p>
 * A {@link ParameterHandler} is used to perform the mapping of a parameter. The
 * handlers are configured via {@link Laf#getParameterHandlers()}. During
 * initialization, the first matching handler for each {@link ParameterInfo} is
 * determined and stored in {@link DefaultUrlMappingRule#}
 * {@link ParameterInfo#getParameterHandler()}.
 * </p>
 */
@Module(exported = { ControllerInfoModule.class,
		ParameterValueProviderModule.class }, imported = {
		InitializationModule.class, ConfigurationModule.class })
@Singleton
public class ParameterHandlerModule {

	public final ConfigurationParameter<List<ParameterHandler>> parameterHandlers = new ConfigurationParameter<List<ParameterHandler>>(
			new ArrayList<ParameterHandler>());

	@Inject
	IntegerParameterHandler integerParameterHandler;

	public void loadDefaultConfiguration(
			@Observes LoadDefaultConfigurationEvent e) {
		parameterHandlers.getValue().add(integerParameterHandler);
	}

}
