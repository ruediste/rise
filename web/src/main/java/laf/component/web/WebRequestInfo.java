package laf.component.web;

import java.util.Deque;

import javax.enterprise.context.RequestScoped;

import laf.core.argumentSerializer.ArgumentSerializerChain;

@RequestScoped
public class WebRequestInfo {
	private String reloadPath;
	private Deque<HtmlTemplateFactory> templateFactories;
	private RequestMapper requestMapper;
	private ArgumentSerializerChain argumentSerializerChain;

	public String getReloadPath() {
		return reloadPath;
	}

	public void setReloadPath(String reloadPath) {
		this.reloadPath = reloadPath;
	}

	public Deque<HtmlTemplateFactory> getTemplateFactories() {
		return templateFactories;
	}

	public void setTemplateFactories(
			Deque<HtmlTemplateFactory> templateFactories) {
		this.templateFactories = templateFactories;
	}

	public RequestMapper getRequestMapper() {
		return requestMapper;
	}

	public void setRequestMapper(RequestMapper requestMapper) {
		this.requestMapper = requestMapper;
	}

	public ArgumentSerializerChain getArgumentSerializerChain() {
		return argumentSerializerChain;
	}

	public void setArgumentSerializerChain(
			ArgumentSerializerChain argumentSerializerChain) {
		this.argumentSerializerChain = argumentSerializerChain;
	}

}
