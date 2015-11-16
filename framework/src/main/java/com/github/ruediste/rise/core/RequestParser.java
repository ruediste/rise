package com.github.ruediste.rise.core;

import com.github.ruediste.rise.core.httpRequest.HttpRequest;

/**
 * Used by the core module to handle requests
 *
 * @see CoreConfiguration#requestParsers
 * @see PathInfoIndex
 */
public interface RequestParser {

    RequestParseResult parse(HttpRequest request);
}
