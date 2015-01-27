package com.github.ruediste.laf.component.web;

import java.util.Deque;

import javax.inject.Inject;

import com.github.ruediste.laf.core.argumentSerializer.ArgumentSerializerChain;

public class WebRequestInfoInitializer implements Runnable {

	@Inject
	WebRequestInfo info;
	private ArgumentSerializerChain argumentSerializerChain;
	private String reloadPath;
	private RequestMapper requestMapper;
	private Deque<HtmlTemplateFactory> templateFactories;

	public void initialize(ArgumentSerializerChain argumentSerializerChain,
			String reloadPath, RequestMapper requestMapper,
			Deque<HtmlTemplateFactory> templateFactories) {
		this.argumentSerializerChain = argumentSerializerChain;
		this.reloadPath = reloadPath;
		this.requestMapper = requestMapper;
		this.templateFactories = templateFactories;
	}

	@Override
	public void run() {
		info.setArgumentSerializerChain(argumentSerializerChain);
		info.setReloadPath(reloadPath);
		info.setRequestMapper(requestMapper);
		info.setTemplateFactories(templateFactories);
	}

}
