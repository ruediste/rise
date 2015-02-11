package com.github.ruediste.laf.core.guice;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.google.inject.*;

/**
 * Container for data stored in the session. There is one instance per session
 */
public class SessionData {

	public static final String SESSION_ATTRIBUTE_KEY = "LAF_SESSION_DATA";
	private static final Object lock = new Object();

	/**
	 * Return the instance attached to the given session
	 */
	public static SessionData get(HttpSession session) {
		Object result = session.getAttribute(SESSION_ATTRIBUTE_KEY);
		if (result == null) {
			synchronized (lock) {
				result = session.getAttribute(SESSION_ATTRIBUTE_KEY);
				if (result == null) {
					result = new SessionData();
					session.setAttribute(SESSION_ATTRIBUTE_KEY, result);
				}
			}
		}
		return (SessionData) result;
	}

	public static SessionData getCurrent() {
		return get(RequestData.getCurrent().getRequest().getSession());
	}

	public static class SessionScopedInstanceContainer {
		private Map<Key<?>, Object> instances = new HashMap<>();

		@SuppressWarnings("unchecked")
		public synchronized <T> T getInstance(Key<T> key, Provider<T> unscoped) {
			Object result;
			if (instances.containsKey(key)) {
				result = instances.get(key);
			} else {
				result = unscoped.get();

				// only store if it is no circular proxy
				if (!Scopes.isCircularProxy(result)) {
					instances.put(key, result);
				}
			}
			return (T) result;
		}
	}

	public final transient SessionScopedInstanceContainer sessionScopedInstanceContainer = new SessionScopedInstanceContainer();
}
