package com.github.ruediste.laf.core.guice;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.github.ruediste.laf.core.entry.*;
import com.github.ruediste.laf.test.ContainerTestBase;
import com.github.ruediste.laf.test.InstanceTestUtil;
import com.github.ruediste.salta.jsr330.Salta;

public class RequestScopedTest extends
		ContainerTestBase<RequestScopedTest.Instance> {

	@RequestScoped
	static class TestRequestScoped {
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	class Instance implements ApplicationInstance {

		@Inject
		TestRequestScoped test1;
		@Inject
		TestRequestScoped test2;

		@Inject
		InstanceTestUtil util;

		@Inject
		HttpScopeManager scopeManager;

		@Override
		public void start() {
			Salta.createInjector(new HttpScopeModule(), new LoggerModule())
					.injectMembers(this);
		}

		@Override
		public void handle(HttpServletRequest request,
				HttpServletResponse response, HttpMethod method)
				throws IOException, ServletException {
			scopeManager.enter(request, response);

			String value = test1.getValue();
			test1.setValue(request.getServletPath());
			util.sendHtmlResponse("" + value + test2.getValue());

			scopeManager.exit();
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub

		}
	}

	@Test
	public void test() {
		WebDriver driver = createDriver();
		driver.get(serverUrl + "foo");
		assertEquals("null/foo", driver.findElement(By.cssSelector("body"))
				.getText());
		driver.get(serverUrl + "foo");
		assertEquals("null/foo", driver.findElement(By.cssSelector("body"))
				.getText());
	}
}
