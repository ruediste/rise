package com.github.ruediste.rise.core.scopes;

import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.GenericEventManager;

@RequestScoped
public class RequestScopeEvents {
    final private GenericEventManager<Object> scopeDestroyedEvent = new GenericEventManager<>();

    /**
     * Return an event which is fired within the {@link RequestScoped request
     * scope} before the request scope is destroyed.
     */
    public GenericEvent<Object> getScopeDestroyedEvent() {
        return scopeDestroyedEvent.event();
    }

    public void fireDestroyed() {
        scopeDestroyedEvent.fire(null);
    }

}
