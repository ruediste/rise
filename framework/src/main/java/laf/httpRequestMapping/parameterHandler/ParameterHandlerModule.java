package laf.httpRequestMapping.parameterHandler;

import javax.inject.Singleton;

import laf.configuration.ConfigurationModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.controllerInfo.ParameterInfo;
import laf.httpRequestMapping.HttpRequestMappingRule;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProviderModule;

import org.jabsaw.Module;

/**
 * <p>
 * In many cases the mapping of URLs can be split in two aspects: There is the
 * overall format of the URL and the way individual parameters are represented.
 * While the configuration of the mapping rules gives you complete freedom of
 * the mapping, we implemented an infrastructure for parameter value handling.
 * This infrastructure is used by the default {@link HttpRequestMappingRule}s,
 * but can as well be used by your own customizations.
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
		ParameterValueProviderModule.class }, imported = { ConfigurationModule.class })
@Singleton
public class ParameterHandlerModule {

}
