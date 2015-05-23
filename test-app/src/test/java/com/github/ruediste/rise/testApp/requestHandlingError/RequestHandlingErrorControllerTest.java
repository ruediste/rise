package com.github.ruediste.rise.testApp.requestHandlingError;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.ruediste.rise.testApp.WebTest;

public class RequestHandlingErrorControllerTest extends WebTest {

	@Test
	public void test() {
		driver.navigate().to(
				url(go(RequestHandlingErrorController.class).index()));
		fail();
	}
}
