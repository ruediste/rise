package com.github.ruediste.laf.component.core;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializerChain;
import com.github.ruediste.laf.core.base.ActionResult;

public class ArgumentLoadingRequestHandler
		extends
		DelegatingRequestHandler<ActionInvocation<String>, ActionInvocation<Object>> {

	private ArgumentSerializerChain argumentSerializerChain;

	public ArgumentLoadingRequestHandler initialize(
			ArgumentSerializerChain argumentSerializerChain) {
		this.argumentSerializerChain = argumentSerializerChain;
		return this;
	}

	@Override
	public ActionResult handle(ActionInvocation<String> invocation) {
		return getDelegate()
				.handle(invocation.map(argumentSerializerChain
						.parseToObjectFunction()));
	}
}
