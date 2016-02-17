package com.github.ruediste.rise.testApp.validation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.test.PageObject;
import com.github.ruediste.rise.testApp.WebTest;

public class ValidationControllerTest extends WebTest {

    private ViewPO po;

    public static class ViewPO extends PageObject {
        private WebElement validateButton = lazy(
                byDataTestName(ValidationController.class, x -> x.pushAndValidate()));
        private WebElement strText = lazy(byDataTestName(
                ValidationController.TestA.class, x -> x.getStr()));
        private WebElement helpBlock = lazy(By.cssSelector(".help-block"));

        public ViewPO setStr(String value) {
            strText.clear();
            strText.sendKeys(value);
            return this;
        }

        public ViewPO checkError() {
            doWait().untilPassing(
                    () -> findElement(By.cssSelector(".form-group.has-error")));
            return this;
        }

        public ViewPO checkValidated() {
            doWait().untilPassing(() -> findElement(
                    By.cssSelector(".form-group.has-success")));
            return this;
        }

        public ViewPO checkNotValidated() {
            doWait().untilTrue(() -> !findElement(By.cssSelector(".form-group"))
                    .getAttribute("class").contains("has-"));
            return this;
        }

        public ViewPO validate() {
            validateButton.click();
            return this;
        }

        public String getHelpMessage() {
            return helpBlock.getText();
        }
    }

    @Before
    public void before() {
        driver.navigate().to(url(go(ValidationController.class).index()));
        po = pageObject(ViewPO.class);
    }

    @Test
    public void openThenValidate() {
        po.checkNotValidated().validate().checkError();
    }

    @Test
    public void checkCustomMessageWorks() {
        po.validate();
        doWait().untilPassing(() -> assertEquals(
                "len must be between 5 and 2147483647", po.getHelpMessage()));
    }

    @Test
    public void validationErrorDisappearsWithCorrectText() {
        po.validate().checkError().setStr("abcdef").validate().checkValidated();
    }

    @Test
    public void initialViolationsPresent() {
        driver.navigate()
                .to(url(go(ValidationController.class).initialValidation()));
        po.checkError();
    }
}
