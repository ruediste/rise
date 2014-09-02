package laf.component.core;

import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.core.base.ActionResult;

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
