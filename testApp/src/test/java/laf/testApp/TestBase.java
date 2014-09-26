package laf.testApp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class TestBase {

	protected String url(String servletPath) {
		return "http://localhost:9090/laf-testApp/" + servletPath;
	}

	protected WebDriver driver() {
		return new HtmlUnitDriver();
	}
}
