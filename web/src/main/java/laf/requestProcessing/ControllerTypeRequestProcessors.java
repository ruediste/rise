package laf.requestProcessing;

import java.util.Map;

import laf.base.configuration.ConfigurationParameter;

/**
 * Configures the {@link RequestProcessor}s used by the
 * {@link SwitchControllerTypeRequestProcessor}
 */
public interface ControllerTypeRequestProcessors extends
		ConfigurationParameter<Map<Object, RequestProcessor>> {

}
