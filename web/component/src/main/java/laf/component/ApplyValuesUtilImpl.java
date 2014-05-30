package laf.component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public class ApplyValuesUtilImpl implements ApplyValuesUtil {

	@Inject
	HttpServletRequest request;

	@Inject
	ComponentService componentService;

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
