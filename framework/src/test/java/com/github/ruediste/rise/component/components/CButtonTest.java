package com.github.ruediste.rise.component.components;

import org.junit.Test;

import com.github.ruediste.rise.core.ActionResult;

public class CButtonTest {

    @Test(expected = IllegalStateException.class)
    public void setHandlerFollowedByTarget() {
        CButton button = new CButton();
        button.setHandler(() -> {
        });
        button.setTarget(new ActionResult() {
        });
    }

    @Test(expected = IllegalStateException.class)
    public void setTargetFollowedByHandler() {
        CButton button = new CButton();
        button.setTarget(new ActionResult() {
        });
        button.setHandler(() -> {
        });
    }
}
