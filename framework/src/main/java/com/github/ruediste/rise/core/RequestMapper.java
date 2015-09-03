package com.github.ruediste.rise.core;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.web.UrlSpec;

public interface RequestMapper {

    /**
     * Generate a HttpRequest for a given action invocation
     * @param sessionId TODO
     */
    UrlSpec generate(ActionInvocation<String> invocation, String sessionId);

    /**
     * Prepare this mapper for use
     */
    void initialize();

}
