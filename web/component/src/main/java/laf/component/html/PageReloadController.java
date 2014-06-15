package laf.component.html;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.Val;
import laf.component.core.Component;
import laf.component.core.ComponentCoreModule;
import laf.component.core.ComponentTreeUtil;
import laf.component.core.ComponentView;
import laf.component.core.ControllerUtilImpl;
import laf.component.core.ControllerUtilProducer;
import laf.component.html.template.HtmlTemplateService;
import laf.component.html.template.RaiseEventsUtil;
import laf.http.RedirectRenderResult;
import laf.http.request.HttpRequest;
import laf.mvc.Controller;

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

	@Inject
	ControllerUtilProducer controllerUtilProducer;

	@Inject
	ControllerUtilImpl controllerUtil;

	public ActionResult reloadPage(long pageId) {

		log.debug("reloading page " + pageId);
		componentCoreModule.setPageId(pageId);
		final PageManager manager = pageMap.get(pageId);
		pageManagerProducer.setPageManager(manager);

		controllerUtil.initialize(manager.persistenceManager);
		controllerUtilProducer.setControllerUtil(null);

		final Val<ActionResult> result = new Val<>();
		manager.persistenceManager.withManager(new Runnable() {

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

				// check if a destination has been defined
				if (controllerUtil.getDestination() != null) {
					result.set(new RedirectRenderResult(controllerUtil
							.getDestination()));
				} else {
					// render result
					componentService
							.renderPage(view, reloadComponent, response);
				}
			}
		});
		return result.get();
	}
}
