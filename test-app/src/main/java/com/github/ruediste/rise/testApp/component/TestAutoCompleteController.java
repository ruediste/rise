package com.github.ruediste.rise.testApp.component;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CAutoComplete;
import com.github.ruediste.rise.component.components.CAutoComplete.AutoSearchMode;
import com.github.ruediste.rise.component.components.CAutoComplete.CAutoCompleteParameters;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.security.urlSigning.UrlUnsigned;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;
import com.google.common.collect.Iterables;

public class TestAutoCompleteController extends ControllerComponent {

    @Label("Auto Complete")
    public static class View extends ViewComponent<TestAutoCompleteController> {

        @Override
        protected Component createComponents() {
            return new CPage(label(this))

            .add(new CAutoComplete<>(
                    new CAutoCompleteParameters<Entry, Integer>() {

                        @Override
                        public List<Entry> search(String term) {
                            return controller.search(term);
                        }

                        @Override
                        public String getSuggestion(Entry item) {
                            return item.getValue();
                        }

                        @Override
                        public String getValue(Entry item) {
                            return item.getValue();
                        }

                        @Override
                        public Integer getId(Entry item) {
                            return item.getId();
                        }

                        @Override
                        public Entry load(Integer id) {
                            return controller.getEntryById(id);
                        }

                        @Override
                        public String getTestName(Entry item) {
                            return String.valueOf(item.getId());
                        }
                    }).setAutoSearchMode(controller.autoSearchMode)
                            .bindItem(() -> controller.data().getEntry()))
                    .add(new CButton(controller, x -> x.pushPull()))
                    .add(new CButton(controller, x -> x.push()))
                    .add(new CButton(controller, x -> x.pull()))
                    .add(toComponentDirect(html -> html.write("Chosen Entry: ")
                            .span().TEST_NAME("chosenEntry").content(String
                                    .valueOf(controller.data().getEntry()))));

        }

    }

    public static class Entry implements Serializable {
        private static final long serialVersionUID = 1L;
        private String value;
        private int id;

        Entry() {
        }

        public Entry(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    ArrayList<Entry> availableEntries = new ArrayList<>();

    {
        availableEntries.add(new Entry(0, "Java"));
        availableEntries.add(new Entry(1, "JavaScript"));
        availableEntries.add(new Entry(2, "Ruby"));
        availableEntries.add(new Entry(3, "C++"));
        availableEntries
                .add(new Entry(4, "<script> alert(\"boom\") </script>"));
    }

    public Entry getEntryById(int id) {
        return Iterables.getOnlyElement(availableEntries.stream()
                .filter(e -> e.getId() == id).collect(toList()), null);
    }

    @Labeled
    public void pushPull() {
        data.pushDown();
        data.pullUp();
    }

    @Labeled
    public void pull() {
        data.pullUp();
    }

    @Labeled
    public void push() {
        data.pushDown();
    }

    public List<Entry> search(String term) {
        return availableEntries.stream().filter(
                e -> e.getValue().toLowerCase().contains(term.toLowerCase()))
                .collect(toList());
    }

    public static class Data {
        private Entry entry;

        public Entry getEntry() {
            return entry;
        }

        public void setEntry(Entry entry) {
            this.entry = entry;
        }
    }

    private BindingGroup<Data> data = new BindingGroup<>(new Data());
    private AutoSearchMode autoSearchMode = AutoSearchMode.SINGLE;

    Data data() {
        return getData().proxy();
    }

    @UrlUnsigned
    public ActionResult index() {
        return null;
    }

    @UrlUnsigned
    public ActionResult index(CAutoComplete.AutoSearchMode autoSearchMode) {
        this.autoSearchMode = autoSearchMode;
        return null;
    }

    public BindingGroup<Data> getData() {
        return data;
    }

    public void setData(BindingGroup<Data> data) {
        this.data = data;
    }
}
