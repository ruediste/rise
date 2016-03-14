package com.github.ruediste.rise.core.scopes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.github.ruediste.rise.core.scopes.HttpScopeManager.SessionScopeManager.SessionScopeData;
import com.github.ruediste.rise.nonReloadable.front.reload.NonReloadable;
import com.github.ruediste.rise.util.GenericEventManager;
import com.github.ruediste.salta.core.Binding;
import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.jsr330.MembersInjector;
import com.github.ruediste.salta.standard.util.SimpleProxyScopeManager;
import com.google.common.base.Preconditions;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;

/**
 * Manager for {@link RequestScoped @RequestScoped} and
 * {@link SessionScoped @SessionScoped}.
 * 
 * <p>
 * <b> Life Cycle </b> <br>
 * The session scope manager supports iterating all session scopes (see
 * {@link #runInEachSessionScope(Runnable)}) and destroy events ( see
 * {@link SessionScopeEvents} )
 * <p>
 * <img src="doc-files/sessionScopeLifeCycle.png" alt=
 * "class diagram of the life cycle handling">
 * <p>
 * When a session is destroyed,
 * {@link SessionScopeData#valueUnbound(HttpSessionBindingEvent)} is invoked,
 * which triggers the {@link SessionScopeData#valueUnbound} event. Upon creation
 * of the session scope data, the {@link SessionScopeManager} registers itself
 * with that event. If it is fired, the scope manager invokes
 * {@link SessionScopeEvents#fireDestroyed(HttpSession)}, which causes the
 * session destroyed event to be triggered. The scope manager makes sure the
 * event is fired within the session scope.
 */
public class HttpScopeManager {
    public static final String SESSION_SCOPE_DATA_KEY = "RISE_SESSION_SCOPE_DATA";
    SessionScopeManager sessionScopeManager = new SessionScopeManager();
    SimpleProxyScopeManager requestScopeHandler = new SimpleProxyScopeManager("Request");

    @PostConstruct
    private void postConstruct(MembersInjector<SessionScopeManager> managerInjector) {
        managerInjector.injectMembers(sessionScopeManager);
    }

    /**
     * Enter the scopes
     */
    public void enter(HttpServletRequest request, HttpServletResponse response) {
        sessionScopeManager.enter(() -> request.getSession());
        requestScopeHandler.setFreshState();
    }

    /**
     * Leave the scope. Always call from a finally block, to make sure scopes
     * are correctly cleaned up
     */
    public void exit() {
        requestScopeHandler.setState(null);
        sessionScopeManager.setState(null);
    }

    public void runInSessionScope(HttpSession session, Runnable run) {
        SessionScopeManager.State old = sessionScopeManager.enter(() -> session);
        try {
            run.run();
        } finally {
            sessionScopeManager.setState(old);
        }
    }

    public void runInEachSessionScope(Runnable run) {
        for (SessionScopeData data : sessionScopeManager.dataMap.values()) {
            SessionScopeManager.State old = sessionScopeManager.enterWithData(() -> data);
            try {
                run.run();
            } finally {
                sessionScopeManager.setState(old);
            }
        }
    }

    static final class SessionScopeManager implements com.github.ruediste.salta.standard.ScopeImpl.ScopeHandler {

        @Inject
        SessionScopeEvents scopeEvents;

        ConcurrentHashMap<String, SessionScopeData> dataMap = new ConcurrentHashMap<>();

        private static Object sessionLock = new Object();

        @NonReloadable
        public static class SessionScopeData implements Serializable, HttpSessionBindingListener {
            private static final long serialVersionUID = 1L;
            public Map<Binding, Object> sessionDataMap = new HashMap<>();
            public GenericEventManager<HttpSessionBindingEvent> valueUnbound = new GenericEventManager<>();

            @Override
            public void valueBound(HttpSessionBindingEvent event) {

            }

            @Override
            public void valueUnbound(HttpSessionBindingEvent event) {
                valueUnbound.fire(event);
            }

        }

        private static class State {
            Supplier<SessionScopeData> dataSupplier;
            SessionScopeData data;

            SessionScopeData getSessionData() {
                if (data == null) {
                    data = dataSupplier.get();
                }
                return data;
            }
        }

        ThreadLocal<State> currentState = new ThreadLocal<>();

        private SessionScopeData getSessionScopeData(HttpSession session) {
            Object result = session.getAttribute(SESSION_SCOPE_DATA_KEY);
            if (result == null) {
                synchronized (sessionLock) {
                    result = session.getAttribute(SESSION_SCOPE_DATA_KEY);
                    if (result == null) {
                        result = createSessionScopeData(session);
                        session.setAttribute(SESSION_SCOPE_DATA_KEY, result);
                    }
                }
            }
            return (SessionScopeData) result;
        }

        private SessionScopeData createSessionScopeData(HttpSession session) {
            SessionScopeData result = new SessionScopeData();
            dataMap.put(session.getId(), result);
            result.valueUnbound.addListener(e -> {
                dataMap.remove(session.getId());
                if (currentState.get() != null)
                    scopeEvents.fireDestroyed(session);
                else {
                    State old = enterWithData(() -> result);
                    try {
                        scopeEvents.fireDestroyed(session);
                    } finally {
                        setState(old);
                    }
                }
            });
            return result;
        }

        public State enter(Supplier<HttpSession> sessionSupplier) {
            return enterWithData(() -> getSessionScopeData(sessionSupplier.get()));
        }

        private State enterWithData(Supplier<SessionScopeData> dataSupplier) {
            State state = new State();
            state.dataSupplier = dataSupplier;
            return setState(state);
        }

        public State setState(State state) {
            State old = currentState.get();
            if (state == null)
                currentState.remove();
            else
                currentState.set(state);
            return old;
        }

        @Override
        public Supplier<Object> scope(Supplier<Object> supplier, Binding binding, CoreDependencyKey<?> requestedKey) {
            // create the proxy right away, such that it can be reused
            // afterwards
            Object proxy = Enhancer.create(requestedKey.getRawType(), new Dispatcher() {

                @Override
                public Object loadObject() throws Exception {
                    State state = currentState.get();
                    Preconditions.checkState(state != null,
                            "Access to session scoped proxy without active session scope");
                    Map<Binding, Object> sessionDataMap = state.getSessionData().sessionDataMap;
                    synchronized (sessionDataMap) {
                        return sessionDataMap.computeIfAbsent(binding, b -> supplier.get());
                    }
                }
            });

            return new Supplier<Object>() {

                @Override
                public Object get() {
                    return proxy;
                }

                @Override
                public String toString() {
                    return "SessionScopeProxy[" + requestedKey + "]";
                }
            };
        }
    }

}