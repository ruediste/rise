package com.github.ruediste.laf.component;

import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.scopes.RequestScoped;

@RequestScoped
public class ComponentRequestInfo {

	private ActionInvocation<String> stringActionInvocation;

	public ActionInvocation<String> getStringActionInvocation() {
		return stringActionInvocation;
	}

	public void setStringActionInvocation(
			ActionInvocation<String> stringActionInvocation) {
		this.stringActionInvocation = stringActionInvocation;
	}

}
