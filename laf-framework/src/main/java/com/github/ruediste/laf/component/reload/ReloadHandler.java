package com.github.ruediste.laf.component.reload;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.api.CView;
import com.github.ruediste.laf.component.ComponentRequestInfo;
import com.github.ruediste.laf.component.ComponentSessionInfo;
import com.github.ruediste.laf.component.ComponentUtil;
import com.github.ruediste.laf.component.PageInfo;
import com.github.ruediste.laf.component.TemplateIndex;
import com.github.ruediste.laf.component.tree.Component;
import com.github.ruediste.laf.component.tree.ComponentTreeUtil;
import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.CoreRequestInfo;
import com.github.ruediste.laf.core.web.ContentRenderResult;

public class ReloadHandler implements Runnable {
	@Inject
	Logger log;

	@Inject
	PageInfo page;

	@Inject
	ComponentUtil util;

	@Inject
	PageReloadRequest request;

	@Inject
	TemplateIndex templateIndex;

	@Inject
	CoreRequestInfo coreRequestInfo;
	@Inject
	ComponentRequestInfo componentRequestInfo;

	@Inject
	ComponentSessionInfo componentSessionInfo;
	@Inject
	CoreConfiguration coreConfiguration;

	@Override
	public void run() {

		log.debug("reloading page " + page.getPageId());

		CView<?> view = page.getView();

		Component reloadComponent = util.getComponent(view,
				request.getComponentNr());

		// apply request values
		List<Component> components = ComponentTreeUtil.subTree(reloadComponent);

		for (Component c : components) {
			templateIndex.getTemplate(c).applyValues(c);
		}

		// process events
		for (Component c : components) {
			templateIndex.getTemplate(c).raiseEvents(c);
		}

		// check if a destination has been defined
		if (componentRequestInfo.getClosePageResult() != null) {
			componentSessionInfo.removePageHandle(page.getPageId());
			coreRequestInfo.setActionResult(componentRequestInfo
					.getClosePageResult());
		} else {
			// render result
			coreRequestInfo.setActionResult(new ContentRenderResult(util
					.renderComponents(view, reloadComponent),
					coreConfiguration.htmlContentType));
		}
	}
}
