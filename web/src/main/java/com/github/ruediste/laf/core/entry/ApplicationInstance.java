package com.github.ruediste.laf.core.entry;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Instance of an application. Will be reloaded when the 
 * application is changed.
 */
public abstract class ApplicationInstance {

	
	public final void start() {
		
		startImpl();
	}

	protected void startImpl() {
		
	}

	public abstract void handle(HttpServletRequest request, HttpServletResponse response,
			HttpMethod method) throws IOException,
			ServletException;

	public final void close() {
		closeImpl();
	}

	protected void closeImpl() {
		
	}
}
