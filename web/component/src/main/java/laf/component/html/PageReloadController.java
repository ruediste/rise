package laf.component.html;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.Controller;
import laf.component.core.*;
import laf.component.html.template.HtmlTemplateService;
import laf.component.html.template.RaiseEventsUtil;
import laf.http.request.HttpRequest;

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
	HttpRequest request;

	@Inject
	HttpServletResponse response;

	@Inject
	ApplyValuesUtilImpl applyValuesUtil;

	@Inject
	RaiseEventsUtil raiseEventsUtil;

	@Inject
	HtmlTemplateService htmlTemplateService;

	@Inject
	PageManagerProducer pageManagerProducer;

	public ActionResult reloadPage(long pageId) {

		log.debug("reloading page " + pageId);
		componentCoreModule.setPageId(pageId);
		final PageManager manager = pageMap.get(pageId);
		pageManagerProducer.setPageManager(manager);

		manager.runInTransaction(new Runnable() {

			@Override
			public void run() {

				ComponentView<?> view = manager.getView();

				long componentId = Long.parseLong(request
						.getParameter("componentId"));
				Component reloadComponent = componentService.getComponent(view,
						componentId);

				// apply request values
				List<Component> components = ComponentTreeUtil
						.subTree(reloadComponent);

				for (Component c : components) {
					applyValuesUtil.setComponent(c);
					htmlTemplateService.getTemplate(c).applyValues(c,
							applyValuesUtil);
				}

				// process events
				for (Component c : components) {
					htmlTemplateService.getTemplate(c).raiseEvents(c,
							raiseEventsUtil);
				}

				// render result
				componentService.renderPage(view, reloadComponent, response);

			}
		});
		return null;
	}
}
