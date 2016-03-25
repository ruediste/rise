package com.github.ruediste.rise.testApp.crud;

import com.github.ruediste.rise.test.PageObject;

public class CrudEditBooleanPO extends PageObject {

    public void set(boolean value) {
        if (rootElement().isSelected() != value)
            rootElement().click();
    }

    public boolean isTrue() {
        return rootElement().isSelected();
    }
}
