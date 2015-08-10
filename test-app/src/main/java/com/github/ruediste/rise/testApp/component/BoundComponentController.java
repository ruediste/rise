package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CFormGroup;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Labeled
public class BoundComponentController extends ControllerComponent {

    @Labeled
    static class View extends ViewComponent<BoundComponentController> {

        @Override
        protected Component createComponents() {
            return new CPage(label(this)).add(toComponent(html -> html
                    .add(toComponentBound(
                            () -> controller.data(),
                            x -> x.span().ID("bound")
                                    .content(controller.data().getValue())))

                    .add(toComponentDirect(x -> x.span().ID("direct")
                            .content(controller.data().getValue())))

                    .add(new CFormGroup(new CTextField().CLASS("textField")
                            .bindText(() -> controller.data().getValue())))

                    .add(new CButton("pushDown").CLASS("pushDown").setHandler(
                            () -> controller.pushDown()))
                    .add(new CButton("pullUp").CLASS("pullUp").setHandler(
                            () -> controller.pullUp()))

            ));
        }

    }

    @PropertiesLabeled
    public static class Data {
        private String value = "";

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private BindingGroup<Data> data = new BindingGroup<>(Data.class);

    public Data data() {
        return data.proxy();
    }

    public ActionResult index() {
        data.set(new Data());

        return null;
    }

    void pushDown() {
        this.data.pushDown();
    }

    void pullUp() {
        data.pullUp();
    }

}
