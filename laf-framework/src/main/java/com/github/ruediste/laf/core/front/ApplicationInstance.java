package com.github.ruediste.laf.core.front;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ApplicationInstance {

	void start();

	void handle(HttpServletRequest request, HttpServletResponse response,
			HttpMethod method) throws IOException, ServletException;

	void close();

}