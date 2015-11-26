package com.github.ruediste.rise.core.scopes;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpSession;

import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.GenericEventManager;

/**
 * Holder for session scope events, namely a destruction event. This class
 * itself is {@link SessionScoped}, thus you can only register event listeners
 * for the current session.
 * 
 * @see HttpScopeManager
 */
@SessionScoped
public class SessionScopeEvents {
    final private GenericEventManager<HttpSession> scopeDestroyedEvent = new GenericEventManager<>();
    private AtomicBoolean destroyedFired = new AtomicBoolean(false);

    /**
     * Return an event which is fired within the {@link SessionScoped session
     * scope} before a session is destroyed
     */
    public GenericEvent<HttpSession> getScopeDestroyedEvent() {
        return scopeDestroyedEvent.event();
    }

    public void fireDestroyed(HttpSession session) {
        if (!destroyedFired.getAndSet(true))
            scopeDestroyedEvent.fire(session);
    }

}
