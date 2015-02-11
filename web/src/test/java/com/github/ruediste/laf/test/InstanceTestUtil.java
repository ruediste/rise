package com.github.ruediste.laf.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

public class InstanceTestUtil {

	@Inject
	private HttpServletResponse response;

	public void sendHtmlResponse(
			String bodyContent) {
		sendResponse("<html><head></head><body>" + bodyContent
				+ "</body></html>");
	}

	public void sendResponse(String content) {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(content);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null)
				out.close();
		}
	}
}
