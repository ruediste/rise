package com.github.ruediste.laf.component.web;

import javax.inject.Inject;

import com.github.ruediste.laf.component.core.tree.Component;
import com.github.ruediste.laf.core.http.CoreRequestInfo;

public class ApplyValuesUtilImpl implements ApplyValuesUtil {

	@Inject
	CoreRequestInfo coreRequestInfo;

	@Inject
	HtmlComponentService componentService;

	private Component component;

	@Override
	public String getValue(String key) {
		return coreRequestInfo.getRequest().getParameter(
				componentService.calculateKey(component, key));
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	@Override
	public boolean isDefined(String key) {
		return coreRequestInfo.getRequest().getParameterMap()
				.containsKey(componentService.calculateKey(component, key));
	}

}
