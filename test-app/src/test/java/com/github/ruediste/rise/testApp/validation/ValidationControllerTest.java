package com.github.ruediste.rise.testApp.validation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.ruediste.rise.test.PageObject;
import com.github.ruediste.rise.test.elementObject.FormGroupEO;
import com.github.ruediste.rise.testApp.WebTest;

public class ValidationControllerTest extends WebTest {

    private ViewPO po;

    public static class ViewPO extends PageObject {
        private WebElement validateButton = lazy(byDataTestName(ValidationController.class, x -> x.pushAndValidate()));

        private WebElement pullUpButton = lazy(byDataTestName(ValidationController.class, x -> x.pullUp()));

        private WebElement strText = lazy(byDataTestName(ValidationController.TestA.class, x -> x.getStr()));

        private WebElement byteArrayText = lazy(
                byDataTestName(ValidationController.TestA.class, x -> x.getByteArray()));

        private WebElement helpBlock = lazy(By.cssSelector(".help-block"));

        public FormGroupEO getStrEO() {
            return pageObject(FormGroupEO.class, strText);
        }

        public FormGroupEO getByteArrayEO() {
            return pageObject(FormGroupEO.class, byteArrayText);
        }

        public ViewPO setStr(String value) {
            strText.clear();
            strText.sendKeys(value);
            return this;
        }

        public ViewPO setByteArray(String value) {
            byteArrayText.clear();
            byteArrayText.sendKeys(value);
            return this;
        }

        public String getByteArrayText() {
            return byteArrayText.getAttribute("value");
        }

        public ViewPO checkErrorStr() {
            doWait().untilTrue(() -> getStrEO().hasValidationError());
            return this;
        }

        public ViewPO checkValidatedStr() {
            doWait().untilTrue(() -> getStrEO().hasValidationSuccess());
            return this;
        }

        public ViewPO checkNotValidatedStr() {
            doWait().untilTrue(() -> !getStrEO().isValidated());
            return this;
        }

        public ViewPO validate() {
            validateButton.click();
            return this;
        }

        public String getHelpMessage() {
            return helpBlock.getText();
        }

        public ViewPO pullUp() {
            pullUpButton.click();
            return this;
        }
    }

    @Before
    public void before() {
        driver.navigate().to(url(go(ValidationController.class).index()));
        po = pageObject(ViewPO.class);
    }

    @Test
    public void openThenValidate() {
        po.checkNotValidatedStr().validate().checkErrorStr();
    }

    @Test
    public void checkCustomMessageWorks() {
        po.validate();
        doWait().untilPassing(() -> assertEquals("len must be between 5 and 2147483647", po.getHelpMessage()));
    }

    @Test
    public void validationErrorDisappearsWithCorrectText() {
        po.validate().checkErrorStr().setStr("abcdef").validate().checkValidatedStr();
    }

    @Test
    public void validationDisappearsWithPullUp() {
        po.validate().checkErrorStr().pullUp().checkNotValidatedStr();
    }

    @Test
    public void byteArrayRoundTrip() {
        po.setByteArray("abcd").validate();
        doWait().untilTrue(() -> po.getByteArrayEO().isValidated());
        po.setByteArray("123");
        doWait().untilPassing(() -> assertEquals("123", po.getByteArrayText()));
        po.pullUp();
        doWait().untilPassing(() -> assertEquals("ABCD", po.getByteArrayText()));
    }

    @Test
    public void byteArrayErrorWhileWritingDown() {
        po.setByteArray("abc").validate();
        doWait().untilTrue(() -> po.getByteArrayEO().hasValidationError());
        doWait().untilPassing(
                () -> assertEquals("Input 'abc' must contain an even number of characters, but length is 3",
                        po.getByteArrayEO().getValidationMessage().get()));
    }

    @Test
    public void initialViolationsPresent() {
        driver.navigate().to(url(go(ValidationController.class).initialValidation()));
        po.checkErrorStr();
    }
}
