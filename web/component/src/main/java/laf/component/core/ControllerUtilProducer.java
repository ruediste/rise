package laf.component.core;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

@RequestScoped
public class ControllerUtilProducer {

	private ControllerUtil controllerUtil;

	@Produces
	@RequestScoped
	public ControllerUtil getControllerUtil() {
		return controllerUtil;
	}

	public void setControllerUtil(ControllerUtil controllerUtil) {
		this.controllerUtil = controllerUtil;
	}
}
