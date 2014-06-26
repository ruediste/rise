package laf.component.reqestProcessing;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.base.ViewTechnologyManager;
import laf.configuration.ConfigurationValue;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.RequestProcessor;

public class InvokeInitialReqestProcessor implements RequestProcessor {
	@Inject
	ConfigurationValue<InvokeInitialReqestProcessorsCP> processors;

	@Inject
	ViewTechnologyManager manager;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		RequestProcessor processor = processors.value().get()
				.get(manager.getViewTechnology());
		if (processor == null) {
			throw new RuntimeException(
					"No request processor found for view technology "
							+ manager.getViewTechnology()
							+ ". Configure a matching processor for InvokeInitialReqestProcessorsCP");
		}
		return processor.process(path);
	}

}
