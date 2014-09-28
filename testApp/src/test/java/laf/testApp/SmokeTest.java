package laf.testApp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SmokeTest extends TestBase {

	@Test
	public void should_login_successfully() throws InterruptedException {
		WebDriver driver = driver("TestComponent");
		assertEquals("Smoke Passed", driver.findElement(By.cssSelector("body"))
				.getText());
	}
}
