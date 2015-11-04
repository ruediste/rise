package com.github.ruediste.rise.core.scopes;

import com.github.ruediste.rise.util.GenericEvent;

@RequestScoped
public class RequestScopeEvents {
    final private GenericEvent<Object> scopeDestroyedEvent = new GenericEvent<>();

    /**
     * Return an event which is fired within the {@link RequestScoped request
     * scope} before the request scope is destroyed.
     */
    public GenericEvent<Object> getScopeDestroyedEvent() {
        return scopeDestroyedEvent;
    }

    public void fireDestroyed() {
        scopeDestroyedEvent.fire(null);
    }

}
