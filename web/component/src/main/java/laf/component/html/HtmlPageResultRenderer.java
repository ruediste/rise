package laf.component.html;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.component.core.*;
import laf.http.requestProcessing.defaultProcessor.ResultRenderer;
import laf.requestProcessing.CurrentController;

public class HtmlPageResultRenderer implements ResultRenderer {

	@Inject
	PageMap pageMap;

	@Inject
	ComponentCoreModule componentCoreModule;

	@Inject
	HtmlComponentService componentService;

	@Inject
	ComponentViewRepository repository;

	@Inject
	CurrentController currentController;

	@Override
	public boolean renderResult(ActionResult result,
			HttpServletResponse response) throws IOException {
		if (!(result instanceof PageActionResult)) {
			return false;
		}

		// create the view
		ComponentView<? extends Object> view = repository
				.createView(currentController.get());

		// assign the page ID
		long id = pageMap.register(view);
		componentCoreModule.setPageId(id);

		// render page
		componentService.renderPage(view, view.getRootComponent(), response);

		return true;
	}
}
