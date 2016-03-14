package com.github.ruediste.rise.core;

import java.util.function.Consumer;

import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;

public class RequestParseResultImpl implements RequestParseResult {

    private Consumer<ActionInvocation<String>> handler;
    private final ActionInvocation<String> invocation;

    public RequestParseResultImpl(ActionInvocation<String> invocation, Consumer<ActionInvocation<String>> handler) {
        this.invocation = invocation;
        this.handler = handler;
    }

    @Override
    public void handle() {
        handler.accept(invocation);
    }

    public ActionInvocation<String> getInvocation() {
        return invocation;
    }

}
