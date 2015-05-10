package com.github.ruediste.rise.mvc.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.api.ControllerMvc;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.PathInfoIndex;
import com.github.ruediste.rise.core.RequestParser;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocationResult;
import com.github.ruediste.rise.core.httpRequest.HttpRequest;
import com.github.ruediste.rise.mvc.MvcConfiguration;
import com.github.ruediste.rise.mvc.MvcRequestParseResult;
import com.github.ruediste.rise.testApp.WebTest;

public class MvcRequestMapperImplTest extends WebTest {

	@Inject
	MvcConfiguration config;

	@Inject
	PathInfoIndex idx;

	@Inject
	Provider<ActionInvocationBuilder> builder;

	@Inject
	CoreUtil coreUtil;

	static class A extends ControllerMvc<A> {

		public ActionResult noArgs() {
			return null;
		}

		public ActionResult withInt(int i) {
			return null;
		}

		public ActionResult withIntLong(int i, Long l) {
			return null;
		}

		public ActionResult withString(String s) {
			return null;
		}
	}

	@Before
	public void setup() {
	}

	@Test
	public void testSimple() {
		check(ctrl().noArgs());
		check(ctrl().withInt(2));
		check(ctrl().withIntLong(2, 3L));
	}

	@Test
	public void testString() {
		check(ctrl().withString("Hello"));
		check(ctrl().withString(null));
		check(ctrl().withString(""));
		check(ctrl().withString("%/+-*"));
	}

	private A ctrl() {
		return builder.get().go(A.class);
	}

	private void check(ActionResult actionResult) {
		ActionInvocationResult invocation = (ActionInvocationResult) actionResult;

		// generate path info
		HttpRequest req = coreUtil.toHttpRequest(invocation);

		// try to parse the generated info
		RequestParser handler = idx.getHandler(req.getPathInfo());
		assertNotNull("No Handler found for " + req, handler);
		MvcRequestParseResult result = (MvcRequestParseResult) handler
				.parse(req);

		// compare parsed result with invocation
		assertTrue(invocation.methodInvocation.isCallToSameMethod(coreUtil
				.toObjectInvocation(result.getInvocation()).methodInvocation));
	}
}
