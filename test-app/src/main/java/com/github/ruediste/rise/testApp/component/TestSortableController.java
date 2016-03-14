package com.github.ruediste.rise.testApp.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CSortable;
import com.github.ruediste.rise.component.components.CText;
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
                    .add(new CSortable<String>().CLASS("list-group").bindItems(() -> controller.data().getItems())
                            .setChildComponentFactory(CText::new).setTestNameExtractor(x -> x))
                    .add(new CButton(controller, x -> x.pullUp())).add(new CButton(controller, x -> x.pushDown()))
                    .add(toComponentDirect(html -> html.span().TEST_NAME("controllerStatus")
                            .write(String.valueOf(controller.data.get().items))._span()));
        }
    }

    public static class Data {
        private List<String> items = new ArrayList<>();

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }

    @Inject
    BindingGroup<Data> data;

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
