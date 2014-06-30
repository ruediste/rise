package laf.controllerInfo;

import java.util.Deque;

import laf.base.configuration.ConfigurationParameter;

public interface ControllerDiscoverers extends
		ConfigurationParameter<Deque<ControllerDiscoverer>> {

}
