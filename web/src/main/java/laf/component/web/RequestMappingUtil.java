package laf.component.web;

import javax.enterprise.context.RequestScoped;

import laf.component.core.ActionInvocation;
import laf.component.web.requestProcessing.RequestMapper;
import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.core.http.request.HttpRequest;

@RequestScoped
public class RequestMappingUtil {

	private RequestMapper mapper;
	private ArgumentSerializerChain chain;

	public void initialize(RequestMapper mapper, ArgumentSerializerChain chain) {
		this.mapper = mapper;
		this.chain = chain;

	}

	public HttpRequest generate(ActionInvocation<Object> invocation) {
		return mapper.generate(invocation.map(chain.generateFunction()));
	}
}
