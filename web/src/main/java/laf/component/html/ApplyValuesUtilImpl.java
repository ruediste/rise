package laf.component.html;

import javax.inject.Inject;

import laf.component.tree.Component;
import laf.http.request.HttpRequest;

public class ApplyValuesUtilImpl implements ApplyValuesUtil {

	@Inject
	HttpRequest request;

	@Inject
	HtmlComponentService componentService;

	private Component component;

	@Override
	public String getValue(String key) {
		return request.getParameter(componentService.calculateKey(component,
				key));
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	@Override
	public boolean isDefined(String key) {
		return request.getParameterMap().containsKey(
				componentService.calculateKey(component, key));
	}

}
