package com.github.ruediste.rise.core;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.web.PathInfo;

public interface RequestMapper {

    /**
     * Generate a HttpRequest for a given action invocation
     */
    PathInfo generate(ActionInvocation<String> invocation);

    /**
     * Prepare this mapper for use
     */
    void initialize();

}
