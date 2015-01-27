package com.github.ruediste.laf.component.web;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class ComponentWebRequestInfo {

	private String contentType;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
