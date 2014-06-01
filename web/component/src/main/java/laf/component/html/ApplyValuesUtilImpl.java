package laf.component.html;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import laf.component.core.Component;

public class ApplyValuesUtilImpl implements ApplyValuesUtil {

	@Inject
	HttpServletRequest request;

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

}
