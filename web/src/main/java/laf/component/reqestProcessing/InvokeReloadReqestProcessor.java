package laf.component.reqestProcessing;

import javax.inject.Inject;

import laf.base.ActionResult;
import laf.base.ViewTechnologyManager;
import laf.base.configuration.ConfigurationValue;
import laf.core.actionPath.ActionPath;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.core.requestProcessing.RequestProcessor;

public class InvokeReloadReqestProcessor implements RequestProcessor {
	@Inject
	ConfigurationValue<InvokeReloadReqestProcessorsCP> processors;

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
							+ ". Configure a matching processor for InvokeReloadProcessorsCP");
		}
		return processor.process(path);
	}

}
