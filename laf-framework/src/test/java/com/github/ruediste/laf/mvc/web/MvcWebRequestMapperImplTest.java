package com.github.ruediste.laf.mvc.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.CoreUtil;
import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.RequestParser;
import com.github.ruediste.laf.core.actionInvocation.ActionInvocationBuilder;
import com.github.ruediste.laf.core.actionInvocation.InvocationActionResult;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.test.SaltaTestBase;

public class MvcWebRequestMapperImplTest extends SaltaTestBase {

	@Inject
	MvcWebConfiguration config;

	@Inject
	PathInfoIndex idx;

	@Inject
	Provider<ActionInvocationBuilder> builder;

	@Inject
	CoreUtil coreUtil;

	static class A implements IControllerMvcWeb {

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
		InvocationActionResult invocation = (InvocationActionResult) actionResult;

		// generate path info
		HttpRequest req = coreUtil.toHttpRequest(invocation);

		// try to parse the generated info
		RequestParser handler = idx.getHandler(req.getPathInfo());
		assertNotNull("No Handler found for " + req, handler);
		MvcWebRequestParseResult result = (MvcWebRequestParseResult) handler
				.parse(req);

		// compare parsed result with invocation
		assertTrue(invocation.methodInvocation.isCallToSameMethod(coreUtil
				.toObjectInvocation(result.getInvocation()).methodInvocation));
	}
}
