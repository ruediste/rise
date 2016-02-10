package com.github.ruediste.rise.testApp.validation;

import javax.inject.Inject;
import javax.validation.Validator;

import org.hibernate.validator.constraints.Length;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.testApp.component.ViewComponent;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

public class ValidationController extends ControllerComponent {

    public static class View extends ViewComponent<ValidationController> {
        @Override
        protected Component createComponents() {

            return new CPage()
                    .add(new CFormGroup().add(new CTextField()
                            .bindText(() -> controller.data().getStr())))
                    .add(new CButton(controller, x -> x.validate()));
        }
    }

    @PropertiesLabeled
    public static class TestA {
        @Length(min = 5)
        private String str = "ab";

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    BindingGroup<TestA> data = new BindingGroup<>(new TestA());

    TestA data() {
        return data.proxy();
    }

    @Inject
    Validator validator;

    @UrlUnsigned
    public ActionResult index() {
        return null;
    }

    @UrlUnsigned
    public ActionResult initialValidation() {
        validate();
        return null;
    }

    @Labeled
    public void validate() {
        data.pushDown();
        setConstraintViolations(data, validator.validate(data.get()));
    }
}
