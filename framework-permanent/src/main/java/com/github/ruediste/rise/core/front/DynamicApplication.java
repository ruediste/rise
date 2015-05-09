package com.github.ruediste.rise.core.front;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.salta.jsr330.Injector;

/**
 * Interface used by the {@link FrontServletBase} to manage the dynamic part of
 * the application.
 */
public interface DynamicApplication {

	void start(Injector permanentInjector);

	void handle(HttpServletRequest request, HttpServletResponse response,
			HttpMethod method) throws IOException, ServletException;

	void close();

}