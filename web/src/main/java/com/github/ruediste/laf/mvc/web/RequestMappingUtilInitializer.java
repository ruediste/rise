package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializerChain;
import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.mvc.core.ActionPath;
import com.github.ruediste.laf.mvc.core.DelegatingRequestHandler;

public class RequestMappingUtilInitializer extends
		DelegatingRequestHandler<String, String> {

	@Inject
	RequestMappingUtil mappingUtil;

	private HttpRequestMapper requestMapper;

	private ArgumentSerializerChain serializerChain;

	public void initialize(HttpRequestMapper requestMapper,
			ArgumentSerializerChain serializerChain) {
		this.requestMapper = requestMapper;
		this.serializerChain = serializerChain;
	}

	@Override
	public ActionResult handle(ActionPath<String> path) {
		mappingUtil.initialize(requestMapper, serializerChain);

		return getDelegate().handle(path);
	}

}
