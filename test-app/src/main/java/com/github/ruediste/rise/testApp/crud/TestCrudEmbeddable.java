package com.github.ruediste.rise.testApp.crud;

import javax.persistence.Embeddable;

import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Labeled
@PropertiesLabeled
@Embeddable
public class TestCrudEmbeddable {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
