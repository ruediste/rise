package laf.component.reqestProcessing;

import javax.inject.Inject;

import laf.base.ActionResult;
import laf.component.core.ComponentConstants;
import laf.component.pageScope.PageScopeManager;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.core.requestProcessing.RequestProcessor;
import laf.mvc.actionPath.ActionPath;

public abstract class SwitchComponentRequestProcessorBase implements
RequestProcessor {

	@Inject
	PageScopeManager pageScopeManager;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		String name = path.getFirst().getMethodInfo().getName();

		Long pageId = null;
		RequestProcessor delegate;

		// determine page id and delegate processor
		if (ComponentConstants.reloadMethodName.equals(name)) {
			pageId = (Long) path.getFirst().getArguments().get(0).get();
			delegate = getReloadProcessor();
		} else if (ComponentConstants.componentActionMethodName.equals(name)) {
			pageId = (Long) path.getFirst().getArguments().get(0).get();
			delegate = getComponentActionProcessor();
		} else {
			delegate = getInitialProcessor();
			pageScopeManager.enterNew();
		}

		// enter the page scope, if it did not happen already (initial processor
		// case)
		if (pageId != null) {
			pageScopeManager.enter(pageId);
		}

		// run the delegate and leave the page scope
		try {
			return delegate.process(path);
		} finally {
			pageScopeManager.leave();
		}
	}

	protected abstract RequestProcessor getReloadProcessor();

	protected abstract RequestProcessor getComponentActionProcessor();

	protected abstract RequestProcessor getInitialProcessor();

}
