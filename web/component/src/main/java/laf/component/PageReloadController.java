package laf.component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.Controller;

@Controller
public class PageReloadController {

	@Inject
	PageMap pageMap;

	@Inject
	ComponentCoreModule componentCoreModule;

	@Inject
	ComponentService componentService;

	@Inject
	HttpServletResponse response;

	ActionResult reloadPage(long pageId) {
		componentCoreModule.setPageId(pageId);
		ComponentView<?> view = pageMap.get(pageId);

		// apply request values
		// process events

		// render result
		componentService.renderPage(view, response);
		return null;
	}
}
