package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;

/**
 * A link to another page.
 */
@DefaultTemplate(CLinkHtmlTemplate.class)
public class CLink extends MultiChildrenComponent<CLink> {

    private ActionResult destination;

    public CLink() {
    }

    public CLink(ActionResult destination) {
        this();
        this.destination = destination;
    }

    public CLink(ActionResult destination, Component child) {
        this(destination);
        add(child);
    }

    public CLink withDestination(ActionResult destination) {
        this.destination = destination;
        return self();
    }

    public ActionResult getDestination() {
        return destination;
    }
}
