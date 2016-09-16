package com.github.ruediste.rise.testApp.component.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CDisplayStack;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.generic.EditComponents;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste.rise.testApp.component.ViewComponent;
import com.github.ruediste.rise.testApp.persistence.TestAppEntity;
import com.github.ruediste1.i18n.label.Labeled;

public class TestEditComponentsController extends ControllerComponent {

    @Labeled
    public static class View extends ViewComponent<TestEditComponentsController> {

        @Inject
        EditComponents editComponents;

        @Override
        protected Component createComponents() {
            return new CPage().render(new CDisplayStack(toComponent(html -> {
                html.div().TEST_NAME("components")
                        .render(editComponents.instance(controller, x -> x.data().getString()).get().getComponent())
                        .render(editComponents.instance(controller, x -> x.data().getEnitities()).get().getComponent())
                        ._div()

                        .div().TEST_NAME("buttons").render(new CButton(controller, x -> x.pushDown()))
                        .render(new CButton(controller, x -> x.pullUp()))._div()

                        .div().TEST_NAME("values")
                        .render(toComponentDirect(x -> x.div().TEST_NAME("string")
                                .content(controller.data.get().getString()).div()
                                .TEST_NAME("enitities").content(controller.data.get().getEnitities().stream()
                                        .map(TestAppEntity::getValue).sorted().collect(Collectors.joining(", ")))))
                        ._div();
            })));
        }

    }

    static class Data {
        private String string;
        private List<TestAppEntity> enitities = new ArrayList<>();

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public List<TestAppEntity> getEnitities() {
            return enitities;
        }

        public void setEnitities(List<TestAppEntity> enitities) {
            this.enitities = enitities;
        }
    }

    @Inject
    BindingGroup<Data> data;

    Data data() {
        return data.proxy();
    }

    @Labeled
    public void pushDown() {
        data.pushDown();
    }

    @Labeled
    public void pullUp() {
        data.pullUp();
    }

    @Inject
    TransactionControl txc;
    @Inject
    EntityManager em;

    @Inject
    Random random;

    @UrlUnsigned
    public ActionResult index() {
        TestAppEntity e = new TestAppEntity();
        e.setValue(Long.toString(random.nextLong()));
        txc.updating().execute(() -> em.persist(e));
        return null;
    }
}
