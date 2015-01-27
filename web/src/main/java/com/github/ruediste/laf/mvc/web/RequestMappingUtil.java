package com.github.ruediste.laf.mvc.web;

import javax.enterprise.context.RequestScoped;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializerChain;
import com.github.ruediste.laf.mvc.core.ActionPath;

@RequestScoped
public class RequestMappingUtil {

	private HttpRequestMapper requestMapper;
	private ArgumentSerializerChain serializerChain;

	public void initialize(HttpRequestMapper requestMapper,
			ArgumentSerializerChain serializerChain) {
		this.requestMapper = requestMapper;
		this.serializerChain = serializerChain;
	}

	public String generate(ActionPath<Object> path) {
		return requestMapper.generate(
				path.mapWithType(serializerChain::generate))
				.getPathWithParameters();
	}

}
