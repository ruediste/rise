package laf.component.web;

import javax.inject.Inject;

import laf.core.argumentSerializer.ArgumentSerializerChain;

public class RequestMappingUtilInitializer implements Runnable {
	@Inject
	RequestMappingUtil util;

	private RequestMapper mapper;
	private ArgumentSerializerChain chain;

	public void initialize(RequestMapper mapper, ArgumentSerializerChain chain) {
		this.mapper = mapper;
		this.chain = chain;

	}

	@Override
	public void run() {
		util.initialize(mapper, chain);
	}
}
