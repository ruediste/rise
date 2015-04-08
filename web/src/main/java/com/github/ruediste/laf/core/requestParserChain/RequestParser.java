package com.github.ruediste.laf.core.requestParserChain;

import com.github.ruediste.laf.core.defaultConfiguration.DefaultConfiguration;
import com.github.ruediste.laf.core.http.request.HttpRequest;

/**
 * Used by the core module to handle requests
 *
 * @see DefaultConfiguration#requestParsers
 * @see DefaultConfiguration#pathInfoIndex
 */
public interface RequestParser {

	RequestParseResult parse(HttpRequest request);
}
