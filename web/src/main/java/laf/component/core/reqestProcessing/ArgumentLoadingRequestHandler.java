package laf.component.core.reqestProcessing;

import laf.component.core.ActionInvocation;
import laf.component.core.DelegatingRequestHandler;
import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.core.base.ActionResult;

public class ArgumentLoadingRequestHandler
		extends
		DelegatingRequestHandler<ActionInvocation<String>, ActionInvocation<Object>> {

	private ArgumentSerializerChain argumentSerializerChain;

	public void initialize(ArgumentSerializerChain argumentSerializerChain) {
		this.argumentSerializerChain = argumentSerializerChain;
	}

	@Override
	public ActionResult handle(ActionInvocation<String> invocation) {
		return getDelegate()
				.handle(invocation.map(argumentSerializerChain
						.parseToObjectFunction()));
	}
}
