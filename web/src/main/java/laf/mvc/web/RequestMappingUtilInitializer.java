package laf.mvc.web;

import javax.inject.Inject;

import laf.base.ActionResult;
import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.mvc.DelegatingRequestHandler;
import laf.mvc.actionPath.ActionPath;

public class RequestMappingUtilInitializer extends
		DelegatingRequestHandler<String, String> {

	@Inject
	RequestMappingUtilImpl mappingUtil;

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
