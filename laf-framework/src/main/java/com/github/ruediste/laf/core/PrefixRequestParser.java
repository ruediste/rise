package com.github.ruediste.laf.core;

import com.github.ruediste.laf.core.httpRequest.HttpRequest;

/**
 * Used by the core module to handle requests
 *
 * @see CoreConfiguration#requestParsers
 * @see CoreConfiguration#pathInfoIndex
 */
public interface PrefixRequestParser {

	RequestParseResult parse(String prefix, HttpRequest request);
}
