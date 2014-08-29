package laf.component.web;

import javax.inject.Inject;

import laf.component.web.requestProcessing.RequestMapper;
import laf.core.argumentSerializer.ArgumentSerializerChain;

public class RequestMappingUtilInitializer {
	@Inject
	RequestMappingUtil util;

	private RequestMapper mapper;
	private ArgumentSerializerChain chain;

	public void initialize(RequestMapper mapper, ArgumentSerializerChain chain) {
		this.mapper = mapper;
		this.chain = chain;

	}

	public void performInitialization() {
		util.initialize(mapper, chain);
	}
}
