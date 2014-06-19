package laf.component;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.RequestProcessor;

public abstract class SwitchComponentRequestProcessorBase implements
		RequestProcessor {

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		String name = path.getFirst().getMethodInfo().getName();

		if (ComponentConstants.reloadMethodName.equals(name)) {
			return getReloadProcessor().process(path);
		} else if (ComponentConstants.componentActionMethodName.equals(name)) {
			return getComponentActionProcessor().process(path);
		} else {
			return getInitialProcessor().process(path);
		}
	}

	protected abstract RequestProcessor getReloadProcessor();

	protected abstract RequestProcessor getComponentActionProcessor();

	protected abstract RequestProcessor getInitialProcessor();

}
