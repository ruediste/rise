package com.github.ruediste.rise.core;

/**
 * Result of a {@link RequestParser}
 */
public interface RequestParseResult {

	/**
	 * Handle the parsed request
	 */
	void handle();
}
