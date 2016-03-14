package com.github.ruediste.rise.core.httpRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.ruediste.rise.core.web.PathInfo;
import com.github.ruediste.rise.core.web.UrlSpec;
import com.github.ruediste.rise.util.Pair;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Mutable implementation of the {@link HttpRequest} interface
 */
public class HttpRequestImpl extends HttpRequestBase {

    private final String pathInfo;
    private final HashMap<String, String[]> parameterMap = new HashMap<>();

    public HttpRequestImpl(UrlSpec spec) {
        this.pathInfo = spec.getPathInfo().getValue();
        Multimap<String, String> tmp = MultimapBuilder.hashKeys().arrayListValues().build();
        for (Pair<String, String> pair : spec.getParameters()) {
            tmp.put(pair.getA(), pair.getB());
        }
        for (Entry<String, Collection<String>> entry : tmp.asMap().entrySet()) {
            parameterMap.put(entry.getKey(), entry.getValue().toArray(new String[] {}));
        }
    }

    public HttpRequestImpl(PathInfo pathInfo) {
        this.pathInfo = pathInfo.getValue();
    }

    @Override
    public String getPathInfo() {
        return pathInfo;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(parameterMap);
    }

    @Override
    public String getParameter(String name) {
        String[] result = parameterMap.get(name);
        if (result == null) {
            return null;
        }
        if (result.length == 0) {
            return null;
        }
        return result[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }

    public Map<String, String[]> getModifiableParameterMap() {
        return parameterMap;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("path", pathInfo).add("parameters", parameterMap).toString();
    }
}
