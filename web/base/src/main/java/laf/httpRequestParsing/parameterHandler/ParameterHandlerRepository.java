package laf.httpRequestParsing.parameterHandler;

import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.attachedProperties.AttachedProperty;
import laf.configuration.ConfigValue;
import laf.controllerInfo.*;

@ApplicationScoped
public class ParameterHandlerRepository {

	/**
	 * Attached property for the parameter handler of a {@link ParameterInfo}
	 */
	AttachedProperty<ParameterInfo, ParameterHandler> parameterHandler = new AttachedProperty<>();

	@Inject
	@ConfigValue("laf.httpRequestParsing.parameterHandler.IntegerParameterHandler")
	LinkedList<ParameterHandler> parameterHandlers;

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
					for (ParameterHandler h : parameterHandlers) {
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
