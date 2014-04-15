package laf.urlMapping;

import javax.inject.Inject;

import laf.LAF;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ControllerInfo;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.ParameterInfo;
import laf.initialization.LafInitializer;

/**
 * Initializer to initialize the {@link ParameterHandler#parameterHandler}
 * property attached to {@link ParameterInfo}s
 *
 * @author ruedi
 *
 */
public class ParameterHandlerInitializer {

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Inject
	LAF laf;

	@LafInitializer
	public void initialize() {
		for (ControllerInfo info : controllerInfoRepository
				.getControllerInfos()) {
			// initialize parameter handlers
			for (ActionMethodInfo method : info.getActionMethodInfos()) {
				for (ParameterInfo parameter : method.getParameters()) {
					for (ParameterHandler h : laf.getParameterHandlers()) {
						if (h.handles(parameter)) {
							ParameterHandler.parameterHandler.set(parameter, h);
							break;
						}
					}
				}
			}
		}
	}
}
