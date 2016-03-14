package com.github.ruediste.rise.test.elementObject;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.test.PageObject;

public class FormGroupEO extends PageObject {

    public WebElement getElement() {
        return rootElement();
    }

    public boolean isValidated() {
        return parent().getAttribute("class").contains("has-");
    }

    public boolean hasValidationError() {
        return parent().getAttribute("class").contains("has-error");
    }

    public boolean hasValidationSuccess() {
        return parent().getAttribute("class").contains("has-success");
    }

    public Optional<String> getValidationMessage() {
        List<WebElement> next = rootElement().findElements(By.xpath("following-sibling::*[@class='help-block']"));
        if (next.size() == 1)
            return Optional.of(next.get(0).getText());
        return Optional.empty();
    }

    private WebElement parent() {
        return rootElement().findElement(By.xpath(".."));
    }
}
