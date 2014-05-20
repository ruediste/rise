package laf.requestProcessing;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

/**
 * Class containing a CDI producer method for the current controller
 */
@RequestScoped
public class CurrentControllerProducer {

	private Object currentController;

	@Produces
	@RequestScoped
	public CurrentController produceCurrentController() {
		return new CurrentController() {

			@Override
			public Object get() {
				return currentController;
			}
		};
	}

	public Object getCurrentController() {
		return currentController;
	}

	public void setCurrentController(Object currentController) {
		this.currentController = currentController;
	}
}
