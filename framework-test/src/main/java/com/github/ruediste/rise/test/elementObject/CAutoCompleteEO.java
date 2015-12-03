package com.github.ruediste.rise.test.elementObject;

import org.openqa.selenium.Keys;

import com.github.ruediste.rise.test.PageObject;

public class CAutoCompleteEO extends PageObject {

    public CAutoCompleteEO setText(String text) {
        actions.click(rootElement()).sendKeys(Keys.END).keyDown(Keys.SHIFT)
                .sendKeys(Keys.HOME).keyUp(Keys.SHIFT).sendKeys(Keys.BACK_SPACE)
                .sendKeys(text).perform();
        return this;
    }

    public CAutoCompleteEO choose(String optionTestName) {
        String testName = rootElement().getAttribute("data-test-name");
        driver.findElement(byDataTestName("rise_autocomplete_" + testName))
                .findElement(byDataTestName(optionTestName)).click();
        return this;
    }

    public CAutoCompleteEO enter() {
        rootElement().sendKeys(Keys.ENTER);
        return this;
    }

    public String getText() {
        return rootElement().getAttribute("value");
    }
}
