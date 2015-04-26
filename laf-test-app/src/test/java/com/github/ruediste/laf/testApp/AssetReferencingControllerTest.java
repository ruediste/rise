package com.github.ruediste.laf.testApp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AssetReferencingControllerTest extends WebTest {

	private WebDriver driver;

	@Before
	public void before() {
		driver = newDriver();
		driver.navigate().to(
				url(path(AssetReferencingController.class).index()));
	}

	@Test
	public void simple() {
		assertEquals("Hello", driver.findElement(By.cssSelector("#data"))
				.getText());
	}

	@Test
	public void cssPresent() {
		checkCssPresent("#samePackage");
		checkCssPresent("#bundleClass");
		checkCssPresent("#default");
		checkCssPresent("#absolute");
	}

	private void checkCssPresent(String id) {
		assertEquals("right", driver.findElement(By.cssSelector(id))
				.getCssValue("text-align"));
	}
}
