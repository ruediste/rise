package com.github.ruediste.rise.test.elementObject;

import com.github.ruediste.rise.test.PageObject;

public class CAutoCompleteEO extends PageObject {

    public CAutoCompleteEO setText(String text) {
        rootElement().clear();
        rootElement().sendKeys(text);
        return this;
    }

    public CAutoCompleteEO choose(String optionTestName) {
        String testName = rootElement().getAttribute("data-test-name");
        driver.findElement(byDataTestName("rise_autocomplete_" + testName))
                .findElement(byDataTestName(optionTestName)).click();
        return this;
    }
}
