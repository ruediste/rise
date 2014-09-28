package laf.testApp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class TestBase {

	protected String url(String servletPath) {
		return "http://localhost:9090/laf-testApp/" + servletPath;
	}

	protected WebDriver driver() {
		return new HtmlUnitDriver(true);
	}

	protected WebDriver driver(String indexKey) {
		WebDriver driver = driver();
		driver.get(url("laf/testApp/testIndex.index"));
		driver.findElement(By.partialLinkText(indexKey)).click();
		return driver;
	}

	protected static class FluentWait {

		long timeout = 5000;
		long sleep = 300;

		private WebDriver driver;

		public FluentWait(WebDriver driver) {
			this.driver = driver;
		}

		public FluentWait timeout(long value) {
			timeout = value;
			return this;
		}

		public FluentWait sleep(long value) {
			sleep = value;
			return this;
		}

		public interface Check {
			boolean check() throws Throwable;
		}

		public void until(Check check) {
			long start = System.currentTimeMillis();
			long nextSleep = sleep;
			while (true) {
				try {
					if (check.check()) {
						return;
					}
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}

				long now = System.currentTimeMillis();
				if ((now - start) >= timeout) {
					throw new AssertionError("Timeout expired");
				}

				long remaining = start + timeout - now;
				if (remaining < 0) {
					throw new AssertionError("Timeout expired");
				}
				if (remaining > nextSleep) {
					remaining = nextSleep;
				}

				nextSleep *= 2;

				try {
					Thread.sleep(remaining);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	protected FluentWait wait(WebDriver driver) {
		return new FluentWait(driver);
	}
}
