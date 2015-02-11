package com.github.ruediste.laf.core.guice;

import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;

import com.google.inject.*;

/**
 * {@link Guice} module binding the {@link SessionScope}, th
 * {@link RequestScope} and various other http request/response related objects.
 */
public class HttpRequestResponseModule extends AbstractModule {

	private static final class SessionScope implements Scope {

		@Inject
		Injector injector;

		@Override
		public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
			return new Provider<T>() {

				ThreadLocal<Boolean> isLocked = new ThreadLocal<>();

				// create the proxy right away, such that it can be reused
				// afterwards
				Object proxy = Enhancer.create(key.getTypeLiteral()
						.getRawType(), new Dispatcher() {

					@Override
					public Object loadObject() throws Exception {
						return SessionData.getCurrent().sessionScopedInstanceContainer
								.getInstance(key, () -> {
									isLocked.set(true);
									try {
										return injector.getInstance(key);
									} finally {
										isLocked.remove();
									}
								});
					}
				});

				@SuppressWarnings("unchecked")
				@Override
				public T get() {
					if (isLocked.get() != null) {
						return unscoped.get();
					}
					return (T) proxy;
				}
			};

		}
	}

	public static class RequestScope implements Scope {
		@Inject
		Injector injector;

		@Override
		public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
			return new Provider<T>() {

				ThreadLocal<Boolean> isLocked = new ThreadLocal<>();

				// create the proxy right away, such that it can be reused
				// afterwards
				Object proxy = Enhancer.create(key.getTypeLiteral()
						.getRawType(), new Dispatcher() {

					@Override
					public Object loadObject() throws Exception {
						return RequestData.getCurrent().requestScopedInstances.computeIfAbsent(
								key,
								(x) -> {
									Set<Key<?>> lockedKeys = RequestData
											.getCurrent().lockedRequestScopeKeys;
									isLocked.set(true);
									try {
										return injector.getInstance(key);
									} finally {
										isLocked.remove();
									}
								});
					}
				});

				@SuppressWarnings("unchecked")
				@Override
				public T get() {
					if (isLocked.get() != null) {
						return unscoped.get();
					}
					return (T) proxy;
				}
			};
		}

	}

	@Override
	protected void configure() {
		install(new LoggerBindingModule());
		install(new PostConstructModule());

		// bind scopes
		{
			SessionScope scope = new SessionScope();
			bindScope(SessionScoped.class, scope);
			bind(SessionScope.class).toInstance(scope);
		}
		{
			RequestScope scope = new RequestScope();
			bindScope(RequestScoped.class, scope);
			bind(RequestScope.class).toInstance(scope);
		}

		bindToRequestData(HttpServletResponse.class, RequestData::getResponse);
		bindToRequestData(HttpServletRequest.class, RequestData::getRequest);

	}

	private <T> void bindToRequestData(Class<T> clazz,
			Function<RequestData, T> func) {
		bind(clazz).toProvider(new Provider<T>() {

			@SuppressWarnings("unchecked")
			T proxy = (T) Enhancer.create(clazz, new Dispatcher() {

				@Override
				public Object loadObject() throws Exception {
					return func.apply(RequestData.getCurrent());
				}
			});

			@Override
			public T get() {
				return proxy;
			}
		});
	}
}