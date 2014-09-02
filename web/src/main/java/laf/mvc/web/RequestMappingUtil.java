package laf.mvc.web;

import javax.enterprise.context.RequestScoped;

import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.mvc.core.ActionPath;

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
				path.mapWithType(serializerChain.generateFunction()))
				.getPathWithParameters();
	}

}
