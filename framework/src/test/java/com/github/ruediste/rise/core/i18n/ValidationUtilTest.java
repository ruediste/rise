package com.github.ruediste.rise.core.i18n;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.constraints.Length;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.rise.core.CurrentLocale;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.LabelUtil;

@RunWith(MockitoJUnitRunner.class)
public class ValidationUtilTest {

    @Mock
    CurrentLocale currentLocale;

    @InjectMocks
    ValidationUtil util;

    private Validator validator;

    private String toString(TranslatedString s) {
        return s.getResourceKey() + ";" + s.getFallback();
    }

    @Before
    public void before() {
        util.patternStringResolver = (s, l) -> s.getPattern().resolve(null);
        util.translatedStringResolver = (s, l) -> toString(s);
        util.labelUtil = new LabelUtil(util.translatedStringResolver);
        when(currentLocale.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        validator = Validation.byDefaultProvider().configure()
                .messageInterpolator(new RiseValidationMessageInterpolator())
                .buildValidatorFactory().getValidator();

    }

    private class TestA {
        @Length(min = 5)
        String value = "ab";
    }

    void checkMessage(String expected, Class<?> classToValidate) {

        Set<ConstraintViolation<TestA>> violations = validator
                .validate(new TestA());
        assertEquals(1, violations.size());
        ConstraintViolation<?> violation = violations.iterator().next();
        assertEquals("org.hibernate.validator.constraints.Length.message;null",
                util.getMessage(violation).resolve(Locale.ENGLISH));
    }

    @Test
    public void testDefaultMessage() {
        checkMessage("org.hibernate.validator.constraints.Length.message;null",
                TestA.class);
    }

    @Label("length of {validatedValue} must be between {min} and {max}")
    public interface CustomMessage extends ValidationMessage {

    }

    private class TestB {
        @Length(min = 5, payload = CustomMessage.class)
        String value = "ab";
    }

    @Test
    public void testCustomMessage() {
        checkMessage(
                "com.github.ruediste.rise.core.i18n.RiseValidationMessageInterpolatorTest$CustomMessage;length of {validatedValue} must be between {min} and {max}",
                TestB.class);
    }

    private class TestC {
        @Length(min = 5, message = "foo")
        String value = "ab";
    }

    @Test
    public void testExplicitMessage() {
        checkMessage("foo", TestA.class);
    }
}
