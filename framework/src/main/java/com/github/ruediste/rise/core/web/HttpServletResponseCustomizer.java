package com.github.ruediste.rise.core.web;

import javax.servlet.http.HttpServletResponse;

/**
 * When a view implements this interface, the implementation will be used to
 * customize the {@link HttpServletResponse}. This allows for example to set
 * custom status codes.
 */
public interface HttpServletResponseCustomizer {
    void customizeServletResponse(HttpServletResponse response);
}