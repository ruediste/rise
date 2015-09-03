package com.github.ruediste.rise.core.web;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * Wrapps a string representing a {@link HttpServletRequest#getPathInfo()
 * pathInfo}
 */
public class PathInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String value;

    public PathInfo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
