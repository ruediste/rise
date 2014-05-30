package laf.component;

import java.util.List;

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

	@Inject
	ApplyValuesUtil applyValuesUtil;

	@Inject
	RaiseEventsUtil raiseEventsUtil;

	public ActionResult reloadPage(long pageId) {
		log.debug("reloading page " + pageId);
		componentCoreModule.setPageId(pageId);
		ComponentView<?> view = pageMap.get(pageId);

		// apply request values
		List<Component> components = ComponentTreeUtil.subTree(view
				.getRootComponent());
		for (Component c : components) {
			c.applyValues(applyValuesUtil);
		}

		// process events
		for (Component c : components) {
			c.raiseEvents(raiseEventsUtil);
		}

		// render result
		componentService.renderPage(view, response);
		return null;
	}
}
