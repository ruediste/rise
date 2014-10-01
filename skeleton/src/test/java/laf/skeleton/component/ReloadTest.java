package laf.skeleton.component;

import static org.junit.Assert.assertEquals;
import laf.testApp.TestBase;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;

public class ReloadTest extends TestBase {

	private WebDriver driver;
	private TestComponentPage page;

	public static class EntityDisplayPage {
		private WebDriver driver;

		EntityDisplayPage(WebDriver driver) {
			this.driver = driver;
		}

		public String stringValue() {
			return driver.findElement(By.id("stringValue")).getText();
		}

		public void refresh() {
			driver.navigate().refresh();
		}
	}

	public static class TestComponentPage {
		private WebDriver driver;

		public TestComponentPage(WebDriver driver) {
			this.driver = driver;
		}

		public String checkerMessage() {
			return driver.findElement(By.id("checkerMessage")).getText();
		}

		public WebElement textInput() {
			return driver.findElement(By.className("stringValue"));
		}

		public WebElement reloadButton() {
			return driver.findElement(By.className("reloadButton"));
		}

		public WebElement saveButton() {
			return driver.findElement(By.className("saveButton"));
		}

		public WebElement pushDownButton() {
			return driver.findElement(By.className("pushDownButton"));
		}

		public WebElement displayEntityLink() {
			return driver.findElement(By.id("displayEntity"));
		}

		public int reloadCount() {
			Integer result = Integer.valueOf(driver.findElement(
					By.className("c_reload")).getAttribute(
					"data-lwf-reload-count"));
			System.out.println(result);
			return result;
		}
	}

	@Before
	public void before() {
		driver = driver("TestComponent");
		page = new TestComponentPage(driver);

	}

	@Test
	public void shouldReloadSucessfully() {
		page.textInput().clear();
		page.textInput().sendKeys("test");
		page.reloadButton().click();
		wait(driver).until(() -> page.reloadCount() == 1);
		assertEquals("test", page.textInput().getAttribute("value"));
	}

	@Test
	public void shouldNotPersistDuringReload() {
		assertEquals("", page.checkerMessage());

		page.textInput().clear();
		page.textInput().sendKeys("test");
		page.saveButton().click();
		wait(driver).until(() -> page.reloadCount() == 1);
		assertEquals("<null>", page.checkerMessage());

		// go to display entity page
		WebDriver d2 = driver();
		d2.get(driver.getCurrentUrl());
		d2.get(page.displayEntityLink().getAttribute("href"));
		EntityDisplayPage displayPage = new EntityDisplayPage(d2);
		assertEquals("test", displayPage.stringValue());

		// prepend foo and push down
		page.textInput().sendKeys("foo");
		page.pushDownButton().click();
		wait(driver).until(() -> page.reloadCount() == 2);

		// check that entity is NOT updated
		displayPage.refresh();
		assertEquals("test", displayPage.stringValue());

		// click save
		page.saveButton().click();
		wait(driver).until(() -> page.reloadCount() == 3);

		// check that entity is updated
		displayPage.refresh();
		assertEquals("footest", displayPage.stringValue());

		// the checker runs in the same trx, but with a different PC
		// thus it does not see the updated value already
		assertEquals("test", page.checkerMessage());
	}
}
