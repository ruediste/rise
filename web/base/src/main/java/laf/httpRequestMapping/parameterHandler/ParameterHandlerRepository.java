package laf.httpRequestMapping.parameterHandler;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.attachedProperties.AttachedProperty;
import laf.controllerInfo.ActionMethodInfo;
import laf.controllerInfo.ControllerInfo;
import laf.controllerInfo.ControllerInfoRepository;
import laf.controllerInfo.ParameterInfo;

@ApplicationScoped
public class ParameterHandlerRepository {

	/**
	 * Attached property for the parameter handler of a {@link ParameterInfo}
	 */
	AttachedProperty<ParameterInfo, ParameterHandler> parameterHandler = new AttachedProperty<>();

	@Inject
	ParameterHandlers parameterHandlers;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	public ParameterHandler getParameterHandler(ParameterInfo info) {
		return parameterHandler.get(info);
	}

	@PostConstruct
	void initialize() {
		for (ControllerInfo info : controllerInfoRepository
				.getControllerInfos()) {
			// initialize parameter handlers
			for (ActionMethodInfo method : info.getActionMethodInfos()) {
				parameterLoop: for (ParameterInfo parameter : method
						.getParameters()) {
					for (ParameterHandler h : parameterHandlers.get()) {
						if (h.handles(parameter)) {
							parameterHandler.set(parameter, h);
							continue parameterLoop;
						}
					}

					throw new RuntimeException("No Handler found for "
							+ parameter);
				}
			}
		}
	}
}
