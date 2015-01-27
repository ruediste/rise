package com.github.ruediste.laf.core.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class RedirectRenderResult implements HttpRenderResult {

	private String path;

	public RedirectRenderResult(String path) {
		this.path = path;
	}

	@Override
	public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
			throws IOException {
		response.sendRedirect(util.httpService.redirectUrl(path));
	}
}
