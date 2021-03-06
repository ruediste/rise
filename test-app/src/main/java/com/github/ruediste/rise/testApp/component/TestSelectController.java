package com.github.ruediste.rise.testApp.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CSelect;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste1.i18n.label.Labeled;

public class TestSelectController extends ControllerComponent {

    @Labeled
    public static class View extends ViewComponent<TestSelectController> {

        @Override
        protected Component createComponents() {
            CText selectedItemText = new CText(String.valueOf(controller.data().getSelectedItem()))
                    .TEST_NAME("viewStatus");
            return new CPage().render(new CSelect<String>().CLASS("list-group")
                    .bindItems(() -> controller.data().getItems()).setAllowEmpty(controller.allowEmpty)
                    .bindSelectedItem(() -> controller.data().getSelectedItem())
                    .setChildComponentFactory(x -> new CText(x)).setTestNameExtractor(x -> x).apply(x -> {
                        if (controller.addSelectionHandler) {
                            x.setSelectionHandler(i -> selectedItemText.setTextString(String.valueOf(i)));
                        }
                    })).render(new CButton(controller, x -> x.pullUp())).render(new CButton(controller, x -> x.pushDown()))
                    .render(new CButton(controller, x -> x.reload()))
                    .render(toComponentDirect(html -> html.span().TEST_NAME("controllerStatus")
                            .addFragmentAndRender(String.valueOf(controller.data.get().getSelectedItem()))._span()))
                    .render(selectedItemText);
        }
    }

    public static class Data {
        private Optional<String> selectedItem = Optional.empty();
        private List<String> items = new ArrayList<>(Arrays.asList("foo", "bar", "fooBar"));

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }

        public Optional<String> getSelectedItem() {
            return selectedItem;
        }

        public void setSelectedItem(Optional<String> selectedItem) {
            this.selectedItem = selectedItem;
        }

    }

    @Inject
    BindingGroup<Data> data;

    Data data() {
        return data.proxy();
    }

    boolean allowEmpty;
    boolean addSelectionHandler;

    @Labeled
    public void reload() {
    }

    @UrlUnsigned
    public ActionResult index() {
        data.get().setSelectedItem(Optional.of("foo"));
        return null;
    }

    @UrlUnsigned
    public ActionResult allowEmpty() {
        allowEmpty = true;
        return null;

    }

    @UrlUnsigned
    public ActionResult selectionHandler() {
        allowEmpty = true;
        addSelectionHandler = true;
        return null;

    }

    @UrlUnsigned
    public ActionResult index(boolean allowEmpty, String initialSelection, boolean addHandler) {
        this.allowEmpty = allowEmpty;
        this.addSelectionHandler = addHandler;
        data.get().setSelectedItem(Optional.ofNullable(initialSelection));
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
