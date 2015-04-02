package com.github.ruediste.laf.core.guice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.servlet.http.*;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;

import org.springframework.web.context.request.RequestScope;

import com.github.ruediste.salta.core.Binding;
import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.jsr330.*;
import com.github.ruediste.salta.standard.ScopeImpl;
import com.github.ruediste.salta.standard.ScopeImpl.ScopeHandler;
import com.github.ruediste.salta.standard.util.SimpleProxyScopeHandler;
import com.google.common.base.Preconditions;

/**
 * {@link Salta} module binding the {@link SessionScopeHandler}, the
 * {@link RequestScope} and various other http request/response related objects.
 */
public class HttpScopeModule extends AbstractModule {

	public static final String SESSION_SCOPE_DATA_KEY = "LAF_SESSION_SCOPE_DATA";

	static final class SessionScopeHandler implements ScopeHandler {

		private static Object sessionLock = new Object();

		private static class State {
			Supplier<HttpSession> sessionSupplier;
			ConcurrentHashMap<Binding, Object> sessionDataMap;

			@SuppressWarnings("unchecked")
			ConcurrentHashMap<Binding, Object> getSessionDataMap() {
				if (sessionDataMap == null) {
					HttpSession session = sessionSupplier.get();
					Object result = session
							.getAttribute(SESSION_SCOPE_DATA_KEY);
					if (result == null) {
						synchronized (sessionLock) {
							result = session
									.getAttribute(SESSION_SCOPE_DATA_KEY);
							if (result == null) {
								result = new ConcurrentHashMap<>();
								session.setAttribute(SESSION_SCOPE_DATA_KEY,
										result);
							}
						}
					}
					sessionDataMap = (ConcurrentHashMap<Binding, Object>) result;
				}
				return sessionDataMap;
			}
		}

		ThreadLocal<State> currentState = new ThreadLocal<>();

		public void enter(Supplier<HttpSession> sessionSupplier) {
			Preconditions.checkState(currentState.get() == null,
					"Already enterd a session scope");
			State state = new State();
			state.sessionSupplier = sessionSupplier;
			currentState.set(state);
		}

		public void exit() {
			Preconditions.checkState(currentState.get() != null,
					"no active session scope");
			currentState.remove();
		}

		@Override
		public Supplier<Object> scope(Supplier<Object> supplier,
				Binding binding, CoreDependencyKey<?> requestedKey) {
			// create the proxy right away, such that it can be reused
			// afterwards
			Object proxy = Enhancer.create(requestedKey.getRawType(),
					new Dispatcher() {

						@Override
						public Object loadObject() throws Exception {
							State state = currentState.get();
							Preconditions
									.checkState(state != null,
											"Access to session scoped proxy without active session scope");
							return state.getSessionDataMap().computeIfAbsent(
									binding, b -> supplier.get());
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

	@RequestScoped
	static class HttpScopeData {
		private HttpServletRequest request;
		private HttpServletResponse response;

		public HttpServletRequest getRequest() {
			return request;
		}

		public void setRequest(HttpServletRequest request) {
			this.request = request;
		}

		public HttpServletResponse getResponse() {
			return response;
		}

		public void setResponse(HttpServletResponse response) {
			this.response = response;
		}
	}

	public static class HttpScopeManagerImpl implements HttpScopeManager {
		SessionScopeHandler sessionScopeHandler = new SessionScopeHandler();
		SimpleProxyScopeHandler requestScopeHandler = new SimpleProxyScopeHandler(
				"Request");

		@Inject
		HttpScopeData scopeData;

		@Override
		public void enter(HttpServletRequest request,
				HttpServletResponse response) {
			sessionScopeHandler.enter(() -> request.getSession());
			requestScopeHandler.enter();
			scopeData.setRequest(request);
			scopeData.setResponse(response);
		}

		@Override
		public void exit() {
			requestScopeHandler.exit();
			sessionScopeHandler.exit();
		}
	}

	@Override
	protected void configure() {

		HttpScopeManagerImpl scopeManager = new HttpScopeManagerImpl();

		bind(HttpScopeManager.class).toInstance(scopeManager);

		// bind scopes
		bindScope(SessionScoped.class, new ScopeImpl(
				scopeManager.sessionScopeHandler));
		bindScope(RequestScoped.class, new ScopeImpl(
				scopeManager.requestScopeHandler));
	}

	@RequestScoped
	@Provides
	HttpServletResponse httpServletResponse(HttpScopeData data) {
		return data.getResponse();
	}

	@RequestScoped
	@Provides
	HttpServletRequest httpServletRequest(HttpScopeData data) {
		return data.getRequest();
	}
}