package com.github.ruediste.rise.testApp.validation;

import javax.inject.Inject;
import javax.validation.Validator;

import org.hibernate.validator.constraints.Length;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.binding.transformers.ByteArrayToHexStringTransformer;
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

    public ValidationController() {
        // TODO Auto-generated constructor stub
    }

    public static class View extends ViewComponent<ValidationController> {
        @Override
        protected Component createComponents() {

            return new CPage().add(new CFormGroup().add(new CTextField().bindText(() -> controller.data().getStr())))
                    .add(new CFormGroup().add(new CTextField().bindText(
                            () -> new ByteArrayToHexStringTransformer().transform(controller.data().getByteArray()))))
                    .add(new CButton(controller, x -> x.pushAndValidate()))
                    .add(new CButton(controller, x -> x.pullUp()));
        }
    }

    @PropertiesLabeled
    public static class TestA {
        @Length(min = 5)
        private String str = "ab";

        private byte[] byteArray;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public byte[] getByteArray() {
            return byteArray;
        }

        public void setByteArray(byte[] byteArray) {
            this.byteArray = byteArray;
        }
    }

    @Inject
    BindingGroup<TestA> data;

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
        pushAndValidate();
        return null;
    }

    @Labeled
    public void pushAndValidate() {
        data.tryPushDown().validate();
    }

    @Labeled
    public void pullUp() {
        data.pullUp();
    }
}
