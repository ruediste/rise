package laf.component.web.defaultConfiguration;

import laf.component.core.DelegatingRequestHandler;
import laf.component.core.reqestProcessing.ComponentActionRequest;
import laf.core.base.configuration.ConfigurationParameter;

public interface ComponentActionPersistenceHandlerCP
		extends
		ConfigurationParameter<DelegatingRequestHandler<ComponentActionRequest, ComponentActionRequest>> {

}
