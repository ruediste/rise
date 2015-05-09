package com.github.ruediste.laf.core.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ContentRenderResult implements HttpRenderResult {
	public final byte[] content;
	public final String contentType;

	public ContentRenderResult(byte[] content, String contentType) {
		this.content = content;
		this.contentType = contentType;
	}

	public ContentRenderResult(String content, String contentType) {
		this.contentType = contentType;
		try {
			this.content = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendTo(HttpServletResponse response, HttpRenderResultUtil util)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(contentType);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(null);
		try (ServletOutputStream out = response.getOutputStream()) {
			out.write(content);
			out.flush();
		}
	}
}
