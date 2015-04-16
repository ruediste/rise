package com.github.ruediste.laf.core.front;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface used by the {@link FrontServletBase} to manage the dynamic part of
 * the application.
 */
public interface DynamicApplication {

	void start();

	void handle(HttpServletRequest request, HttpServletResponse response,
			HttpMethod method) throws IOException, ServletException;

	void close();

}