package laf.mvc.web;

import javax.enterprise.context.RequestScoped;

import laf.core.argumentSerializer.ArgumentSerializerChain;
import laf.mvc.actionPath.ActionPath;

@RequestScoped
public class RequestMappingUtilImpl implements RequestMappingUtil {

	private HttpRequestMapper requestMapper;
	private ArgumentSerializerChain serializerChain;

	public void initialize(HttpRequestMapper requestMapper,
			ArgumentSerializerChain serializerChain) {
		this.requestMapper = requestMapper;
		this.serializerChain = serializerChain;
	}

	@Override
	public String generate(ActionPath<Object> path) {
		return requestMapper.generate(
				path.mapWithType(serializerChain.generateFunction()))
				.getPathWithParameters();
	}

}
