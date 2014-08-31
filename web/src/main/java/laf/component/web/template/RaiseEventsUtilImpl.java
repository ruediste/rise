package laf.component.web.template;

import javax.inject.Inject;

import laf.component.core.tree.Component;
import laf.component.web.HtmlComponentService;
import laf.component.web.api.CWRaiseEventsUtil;
import laf.core.http.CoreRequestInfo;

public class RaiseEventsUtilImpl implements CWRaiseEventsUtil {

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	HtmlComponentService componentService;

	private Component component;

	@Override
	public String getValue(String key) {
		return coreRequestInfo.getRequest().getParameter(
				componentService.calculateKey(getComponent(), key));
	}

	@Override
	public boolean isDefined(String key) {
		return coreRequestInfo.getRequest().getParameterMap()
				.containsKey(componentService.calculateKey(component, key));
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

}
