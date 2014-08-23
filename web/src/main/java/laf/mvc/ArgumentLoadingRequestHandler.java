package laf.mvc;

import laf.base.ActionResult;
import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.mvc.actionPath.ActionPath;

public class ArgumentLoadingRequestHandler extends
		DelegatingRequestHandler<String, Object> {

	private ArgumentSerializerChain chain;

	public void initialize(ArgumentSerializerChain chain) {
		this.chain = chain;

	}

	@Override
	public ActionResult handle(ActionPath<String> path) {
		return getDelegate().handle(
				path.mapWithType(chain.parseToObjectFunction()));
	}

}