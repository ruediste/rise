package com.github.ruediste.laf.core.web;

import javax.inject.Inject;

import com.github.ruediste.laf.core.HttpService;

public class HttpRenderResultUtil {
	@Inject
	public HttpService httpService;

	public HttpService getHttpService() {
		return httpService;
	}

}
