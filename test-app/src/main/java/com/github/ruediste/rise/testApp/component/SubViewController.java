package com.github.ruediste.rise.testApp.component;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

public class SubViewController extends ControllerComponent {

    @Labeled
    static class View extends ViewComponent<SubViewController> {

        @Override
        protected Component createComponents() {
            // @formatter:off
            return new CPage(label(this))
                    .add(toSubView(() -> controller.data(),x->x.getSubController()))
                    .add(new CButton("Sub1").CLASS("sub1").handler(() -> controller.showController1()))
                    .add(new CButton("Sub2").CLASS("sub2").handler(() -> controller.showController2()));
            // @formatter:on
        }
    }

    @PropertiesLabeled
    static class SubData {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    static class SubController {

        BindingGroup<SubData> data = new BindingGroup<>(new SubData());

        public SubController() {
        }

        public SubController(String string) {
            data.get().setText(string);
        }

        SubData data() {
            return data.proxy();
        }
    }

    static class SubView extends ViewComponent<SubController> {

        @Override
        protected Component createComponents() {
            return toComponent(html -> html
                    .span()
                    .CLASS("subText")
                    .add(new CText().CLASS("subText").bindText(
                            () -> controller.data().getText()))._span());
        }

    }

    static class Data {
        private SubController subController;

        public SubController getSubController() {
            return subController;
        }

        public void setSubController(SubController subController) {
            this.subController = subController;
        }
    }

    SubController sub1 = new SubController("Foo");
    SubController sub2 = new SubController("bar");

    BindingGroup<Data> data = new BindingGroup<>(new Data());

    Data data() {
        return data.proxy();
    }

    public ActionResult index() {
        data.get().setSubController(sub1);
        return null;
    }

    void showController1() {
        data.get().setSubController(sub1);
        data.pullUp();
    }

    void showController2() {
        data.get().setSubController(sub2);
        data.pullUp();
    }
}
