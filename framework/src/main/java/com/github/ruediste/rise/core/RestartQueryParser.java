package com.github.ruediste.rise.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.rise.core.front.ReloadCountHolder;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;

public final class RestartQueryParser implements RequestParser {

	@Inject
	ReloadCountHolder holder;

	@Inject
	CoreRequestInfo info;

	@Override
	public RequestParseResult parse(HttpRequest request) {

		return new RequestParseResult() {

			@Override
			public void handle() {
				boolean doReload = holder.waitForReload(Long.parseLong(request
						.getParameter("nr")));
				HttpServletResponse response = info.getServletResponse();

				response.setContentType("text/plain;charset=utf-8");
				PrintWriter out;
				try {
					out = response.getWriter();
					out.write(doReload ? "true" : "false");
					out.close();
				} catch (IOException e) {
					throw new RuntimeException("Error sending response");
				}
			}
		};
	}
}