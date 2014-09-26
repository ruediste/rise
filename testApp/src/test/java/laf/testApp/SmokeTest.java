package laf.testApp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SmokeTest extends TestBase {

	@Test
	public void should_login_successfully() {
		WebDriver driver = driver();
		driver.get(url("laf/testApp/smokeTest/smokeTest.index"));

		assertEquals("Smoke Passed", driver.findElement(By.cssSelector("body"))
				.getText());
	}
}
