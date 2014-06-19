package laf.component;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.RequestProcessor;

public class SwitchComponentRequestProcessor implements RequestProcessor {

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		if (path.getFirst()) {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
