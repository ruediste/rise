package laf.mvc.core;

import javax.inject.Inject;

import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.core.base.ActionResult;
import laf.mvc.core.actionPath.ActionPath;

public class ArgumentLoadingRequestHandler extends
		DelegatingRequestHandler<String, Object> {

	@Inject
	MvcRequestInfo requestInfo;

	private ArgumentSerializerChain chain;

	public void initialize(ArgumentSerializerChain chain) {
		this.chain = chain;

	}

	@Override
	public ActionResult handle(ActionPath<String> path) {
		ActionPath<Object> objectActionPath = path.mapWithType(chain
				.parseToObjectFunction());
		requestInfo.setObjectActionPath(objectActionPath);
		return getDelegate().handle(objectActionPath);
	}

}
