package com.github.ruediste.rise.sample.component;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.sample.Icon;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

public class SampleComponentController extends ControllerComponent {

    int counter;

    @Inject
    BindingGroup<Data> data;

    @PropertiesLabeled
    public static class Data {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public Data getData() {
        return data.proxy();
    }

    void inc() {
        counter++;
        data.pushDown();
        System.out.println(data.get().getText());
    }

    @Label("Component Sample")
    @Icon(Glyphicon.star)
    public ActionResult index() {
        data.set(new Data());
        data.get().setText("Hello World");
        return null;
    }
}
