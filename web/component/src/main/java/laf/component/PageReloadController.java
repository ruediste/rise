package laf.component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.Controller;

import org.slf4j.Logger;

@Controller
public class PageReloadController {

	@Inject
	Logger log;

	@Inject
	PageMap pageMap;

	@Inject
	ComponentCoreModule componentCoreModule;

	@Inject
	ComponentService componentService;

	@Inject
	HttpServletResponse response;

	public ActionResult reloadPage(long pageId) {
		log.debug("reloading page " + pageId);
		componentCoreModule.setPageId(pageId);
		ComponentView<?> view = pageMap.get(pageId);

		// apply request values

		// process events

		// render result
		componentService.renderPage(view, response);
		return null;
	}
}
