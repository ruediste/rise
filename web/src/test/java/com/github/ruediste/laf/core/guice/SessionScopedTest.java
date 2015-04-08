package com.github.ruediste.laf.core.guice;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.github.ruediste.laf.core.entry.ApplicationInstance;
import com.github.ruediste.laf.core.entry.HttpMethod;
import com.github.ruediste.laf.test.ContainerTestBase;
import com.github.ruediste.laf.test.InstanceTestUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class SessionScopedTest extends
		ContainerTestBase<SessionScopedTest.Instance> {

	@SessionScoped
	static class TestSessionScoped {

		@Inject
		TestSessionScoped2 test2;

		@Inject
		Provider<TestSessionScoped> selfInjected;

		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void check() {
			assertTrue("Identical instances", this == selfInjected.get().self());
			test2.check(this);
		}

		public TestSessionScoped self() {
			return this;
		}
	}

	@SessionScoped
	static class TestSessionScoped2 {
		@Inject
		Provider<TestSessionScoped> test1;

		public void check(TestSessionScoped value) {
			assertTrue(test1.get() != value);
		}
	}

	static class Instance implements ApplicationInstance {

		@Inject
		TestSessionScoped test;

		@Inject
		InstanceTestUtil util;

		@Inject
		HttpScopeManager scopeManager;

		@Override
		public void start() {
			Injector injector = Salta.createInjector(new HttpScopeModule());
			injector.injectMembers(this);
		}

		@Override
		public void handle(HttpServletRequest request,
				HttpServletResponse response, HttpMethod method)
				throws IOException, ServletException {
			scopeManager.enter(request, response);

			test.check();

			util.sendHtmlResponse(test.getValue());

			test.setValue(request.getServletPath());

			scopeManager.exit();
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub

		}

	}

	@Test
	public void test() {

		// let test run in first session
		HtmlUnitDriver driver1 = new HtmlUnitDriver(false);
		HtmlUnitDriver driver2 = new HtmlUnitDriver(false);

		// first request/response
		makeRequest(driver1, "foo", "null");
		makeRequest(driver1, "bar", "/foo");

		// check if session 2 is isolated
		makeRequest(driver2, "foo", "null");
		makeRequest(driver2, "bar", "/foo");

		// use first session again, to check there are no side effects
		makeRequest(driver1, "bar2", "/bar");
	}

	protected void makeRequest(WebDriver driver, String request, String expected) {
		driver.get(serverUrl + request);
		assertEquals(expected, driver.findElement(By.cssSelector("body"))
				.getText());
	}
}
