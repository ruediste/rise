package com.github.ruediste.laf.mvc.core;

import javax.inject.Inject;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializerChain;
import com.github.ruediste.laf.core.base.ActionResult;

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
