package com.github.ruediste.rise.testApp.component;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CAutoComplete;
import com.github.ruediste.rise.component.components.CAutoComplete.AutoCompleteValue;
import com.github.ruediste.rise.component.components.CAutoComplete.AutoSearchMode;
import com.github.ruediste.rise.component.components.CAutoComplete.CAutoCompleteParameters;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CClickEdit;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.components.CTextField;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;

public class TestClickEditController extends ControllerComponent {

    @Label("ClickEdit")
    public static class View extends ViewComponent<TestClickEditController> {

        @Override
        protected Component createComponents() {
            return new CPage(label(this))
                    .render(new CClickEdit<String>(v -> new CText(v).TEST_NAME("viewText"), (v, c) -> {
                        CTextField tf = new CTextField().setText(v);
                        c.setEditComponent(tf, () -> tf.getText(), tf);
                    }).bindValue(() -> controller.data().getTestLine()))
                    .render(new CClickEdit<AutoCompleteValue<TestClickEditController.Entry>>(
                            v -> new CText(v.isItemChosen() ? v.getItem().name : v.getText()).TEST_NAME("viewText"),
                            (v, c) -> {
                                CAutoComplete<Entry, Integer> auto = new CAutoComplete<>(
                                        new CAutoCompleteParameters<Entry, Integer>() {

                                            @Override
                                            public List<Entry> search(String term) {
                                                return controller.entries.stream().filter(e -> e.name.contains(term))
                                                        .collect(toList());
                                            }

                                            @Override
                                            public String getSuggestion(Entry item) {
                                                return item.name;
                                            }

                                            @Override
                                            public String getValue(Entry item) {
                                                return item.name;
                                            }

                                            @Override
                                            public Integer getId(Entry item) {
                                                return item.id;
                                            }

                                            @Override
                                            public Entry load(Integer id) {
                                                return controller.entries.stream().filter(e -> e.id == id).findFirst()
                                                        .orElse(null);
                                            }

                                            @Override
                                            public String getTestName(Entry item) {
                                                return item.name;
                                            }
                                        }).setValue(v).setAutoSearchMode(AutoSearchMode.SINGLE);
                                c.setEditComponent(auto, () -> auto.getValue(), auto);
                            }).bindValue(() -> controller.data().getAutoComplete()))
                    .render(new CText("clickTarget").TEST_NAME("clickTarget")).render(
                            new CButton(controller,
                                    x -> x.push()))
                    .render(new CButton(controller, x -> x.pull()))
                    .render(toComponentDirect(html -> html.addFragmentAndRender("Line: ").span().TEST_NAME("testLine")
                            .content(String.valueOf(controller.data().getTestLine())).addFragmentAndRender("AutoCompleteValue: ")
                            .span().TEST_NAME("autoCompleteValue")
                            .content(String.valueOf(controller.data().getAutoComplete()))));

        }

    }

    @Labeled
    public void pull() {
        data.pullUp();
    }

    @Labeled
    public void push() {
        data.pushDown();
    }

    public static class Entry {
        String name;
        int id;

        public Entry(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return "" + id + ":" + name;
        }
    }

    List<Entry> entries = new ArrayList<>(
            Arrays.asList(new Entry("Java", 0), new Entry("Ruby", 1), new Entry("JavaScript", 2)));

    public static class Data {
        private String testLine;
        private AutoCompleteValue<Entry> autoComplete = AutoCompleteValue.ofText("autocomplete");

        public String getTestLine() {
            return testLine;
        }

        public void setTestLine(String testLine) {
            this.testLine = testLine;
        }

        public AutoCompleteValue<Entry> getAutoComplete() {
            return autoComplete;
        }

        public void setAutoComplete(AutoCompleteValue<Entry> autoComplete) {
            this.autoComplete = autoComplete;
        }

    }

    @Inject
    private BindingGroup<Data> data;

    Data data() {
        return getData().proxy();
    }

    @UrlUnsigned
    public ActionResult index() {
        return null;
    }

    public BindingGroup<Data> getData() {
        return data;
    }

    public void setData(BindingGroup<Data> data) {
        this.data = data;
    }
}
