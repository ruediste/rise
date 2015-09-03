package com.github.ruediste.rise.core.httpRequest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;

/**
 * A reduced view of a {@link HttpServletRequest}
 * 
 * *
 * <p>
 * The {@link javax.servlet.http.HttpServletRequest} interface is quite large.
 * For the purpose of this framework, it makes sense to define a more focused
 * version of that interface. This simplifies testing, too.
 * </p>
 */
public interface HttpRequest {

    /**
     * Return the path of this request. Initialized form
     * {@link HttpServletRequest#getPathInfo()}
     */
    String getPathInfo();

    /**
     * @see HttpServletRequest#getParameterMap()
     */
    Map<String, String[]> getParameterMap();

    /**
     * @see HttpServletRequest#getParameter(String)
     */
    String getParameter(String name);

    /**
     * @see HttpServletRequest#getParameterValues(String)
     */
    String[] getParameterValues(String name);

    /**
     * Check for equality of the {@link #getPathInfo()} and the
     * {@link #getParameterMap()}
     */
    @Override
    public boolean equals(Object obj);

    /**
     * Return {@link #getPathInfo()} with the parameters appended
     */
    String getPathWithParameters();

    default UrlSpec createUrlSpec() {
        return new UrlSpec(new PathInfo(getPathInfo()), getParameterMap());
    }
}
