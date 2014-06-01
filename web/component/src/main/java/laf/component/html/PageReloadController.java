package laf.component.html;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.Controller;
import laf.component.core.Component;
import laf.component.core.ComponentCoreModule;
import laf.component.core.ComponentTreeUtil;
import laf.component.core.ComponentView;
import laf.component.html.template.HtmlTemplateService;
import laf.component.html.template.RaiseEventsUtil;

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
	HtmlComponentService componentService;

	@Inject
	HttpServletResponse response;

	@Inject
	ApplyValuesUtilImpl applyValuesUtil;

	@Inject
	RaiseEventsUtil raiseEventsUtil;

	@Inject
	HtmlTemplateService htmlTemplateService;

	public ActionResult reloadPage(long pageId) {
		log.debug("reloading page " + pageId);
		componentCoreModule.setPageId(pageId);
		ComponentView<?> view = pageMap.get(pageId);

		// apply request values
		List<Component> components = ComponentTreeUtil.subTree(view
				.getRootComponent());
		for (Component c : components) {
			applyValuesUtil.setComponent(c);
			htmlTemplateService.getTemplate(c).applyValues(c, applyValuesUtil);
		}

		// process events
		for (Component c : components) {
			htmlTemplateService.getTemplate(c).raiseEvents(c, raiseEventsUtil);
		}

		// render result
		componentService.renderPage(view, response);
		return null;
	}
}
