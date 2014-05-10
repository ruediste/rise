package laf.httpRequestMapping.parameterHandler;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.controllerInfo.*;
import laf.initialization.LafInitializer;
import laf.initialization.laf.LafInitializationPhase;

/**
 * Initializer to initialize the {@link ParameterHandler#parameterHandler}
 * property attached to {@link ParameterInfo}s
 *
 * @author ruedi
 *
 */
@Singleton
public class ParameterHandlerInitializer {

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	@Inject
	ParameterHandlerModule parameterHandlerModule;

	@LafInitializer(phase = LafInitializationPhase.class, after = ControllerInfoRepositoryInitializer.class)
	public void initialize() {
		List<ParameterHandler> handlers = parameterHandlerModule.parameterHandlers;
		for (ControllerInfo info : controllerInfoRepository
				.getControllerInfos()) {
			// initialize parameter handlers
			for (ActionMethodInfo method : info.getActionMethodInfos()) {
				for (ParameterInfo parameter : method.getParameters()) {
					for (ParameterHandler h : handlers) {
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
