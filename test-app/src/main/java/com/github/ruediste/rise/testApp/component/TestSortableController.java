package com.github.ruediste.rise.testApp.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CSortable;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste1.i18n.label.Labeled;

public class TestSortableController extends ControllerComponent {

    @Labeled
    public static class View extends ViewComponent<TestSortableController> {

        @Override
        protected Component createComponents() {
            return new CPage()
                    .add(new CSortable<String>()
                            .bind(c -> c.setItems(controller.data().getItems()))
                            .setChildComponentFactory(
                                    x -> toComponent(html -> html.write(x))))
                    .add(new CButton(controller, x -> x.pullUp()))
                    .add(new CButton(controller, x -> x.pushDown()))
                    .add(toComponentDirect(html -> html.write(
                            String.valueOf(controller.data.get().items))));
        }
    }

    static class Data {
        private List<String> items = new ArrayList<>();

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

    BindingGroup<Data> data = new BindingGroup<>(new Data());

    Data data() {
        return data.proxy();
    }

    @UrlUnsigned
    public ActionResult index() {
        data.get().items.addAll(Arrays.asList("foo", "bar", "fooBar"));
        return null;
    }

    @Labeled
    public void pushDown() {
        data.pushDown();
    }

    @Labeled
    public void pullUp() {
        data.pullUp();
    }
}
