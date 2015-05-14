package com.github.ruediste.rise.core.aop;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Salta;

public class AopUtilTest {

	boolean invoked;

	@Before
	public void before() {
		invoked = false;
	}

	private static class A {
		void foo() {
		}
	}

	private class I extends Interceptor<Object> {

		@Override
		public InterceptorResultBefore<Object> onBefore(
				InterceptedInvocation invocation) {
			return null;
		}

		@Override
		public InterceptorResultOther<Object> onSuccess(Object arg,
				InterceptedInvocation invocation, Object result) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public InterceptorResultOther<Object> onFailure(Object arg,
				InterceptedInvocation invocation, Throwable t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public InterceptorResultOther<Object> onComplete(Object arg,
				InterceptedInvocation invocation) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Test
	public void simpleAdvice() {
		A a = Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() throws Exception {
				AopUtil.register(binder(), new Interceptor() {
				});
			}
		}).getInstance(A.class);
		a.foo();
		assertTrue(invoked);
	}
}
