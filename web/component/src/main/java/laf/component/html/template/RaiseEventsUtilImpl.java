package laf.component.html.template;

import javax.inject.Inject;

import laf.component.core.Component;
import laf.component.html.HtmlComponentService;
import laf.http.request.HttpRequest;

public class RaiseEventsUtilImpl implements RaiseEventsUtil {

	@Inject
	HttpRequest request;

	@Inject
	HtmlComponentService componentService;

	private Component component;

	@Override
	public String getValue(String key) {
		return request.getParameter(componentService.calculateKey(
				getComponent(), key));
	}

	@Override
	public boolean isDefined(String key) {
		return request.getParameterMap().containsKey(
				componentService.calculateKey(component, key));
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

}
