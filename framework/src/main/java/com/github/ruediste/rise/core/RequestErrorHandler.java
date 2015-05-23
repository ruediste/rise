package com.github.ruediste.rise.core;

/**
 * Handler for errors occurring during a request.
 */
public interface RequestErrorHandler {

	/**
	 * Handle an error. Request information can be accessed via
	 * {@link CoreRequestInfo}. The exception is stored in
	 * {@link CoreRequestInfo#getRequestError()}
	 */
	void handle();
}
