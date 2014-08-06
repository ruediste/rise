package laf.requestProcessing;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.base.configuration.ConfigurationValue;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public class SwitchControllerTypeRequestProcessor implements RequestProcessor {

	@Inject
	ConfigurationValue<ControllerTypeRequestProcessorsCP> processors;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		Object type = path.getFirst().getControllerInfo().getType();
		RequestProcessor requestProcessor = processors.value().get().get(type);

		if (requestProcessor == null) {
			throw new RuntimeException(
					"No RequestProcessor configured for controller "
							+ path.getFirst().getControllerInfo() + "of type "
							+ type);
		}
		return requestProcessor.process(path);
	}

}
