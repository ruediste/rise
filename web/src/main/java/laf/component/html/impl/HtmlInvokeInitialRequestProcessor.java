package laf.component.html.impl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.component.core.*;
import laf.component.core.impl.ControllerUtilImpl;
import laf.component.html.HtmlComponentService;
import laf.component.reqestProcessing.InitialControllerInvokerCP;
import laf.component.reqestProcessing.InitialParameterLoaderCP;
import laf.configuration.ConfigurationValue;
import laf.http.RedirectRenderResult;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.*;

public class HtmlInvokeInitialRequestProcessor extends
		LoadAndInvokeRequestProcessor {

	@Inject
	ConfigurationValue<InitialParameterLoaderCP> loader;

	@Inject
	ConfigurationValue<InitialControllerInvokerCP> invoker;

	@Inject
	ControllerUtilImpl controllerUtil;

	@Inject
	HtmlComponentService componentService;

	@Inject
	ComponentViewRepository viewRepository;

	@Inject
	CurrentController currentController;

	@Inject
	Page page;

	@Inject
	HttpServletResponse response;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		if (path.getElements().size() > 1) {
			throw new RuntimeException(
					"Embedded controllers not allowed for component controllers. ActionPath: "
							+ path);
		}
		ActionResult result = super.process(path);
		// check if a destination has been defined
		if (controllerUtil.getDestination() != null) {
			return new RedirectRenderResult(controllerUtil.getDestination());
		} else {

			ComponentView<Object> view = viewRepository
					.createView(currentController.get());

			page.setView(view);

			// render result
			componentService
			.renderPage(view, view.getRootComponent(), response);
			return null;
		}
	}

	@Override
	public ParameterLoader getLoader() {
		return loader.value().get();
	}

	@Override
	public ControllerInvoker getInvoker() {
		return invoker.value().get();
	}

}