package com.github.ruediste.rise.component.components;

import java.util.Collections;

import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentViewRepository;
import com.github.ruediste.rise.component.components.template.CControllerTemplate;
import com.github.ruediste.rise.component.tree.Component;

/**
 * Embeds the view of a controller
 */
@DefaultTemplate(CControllerTemplate.class)
public class CController extends AttachedPropertyBearerBase implements
        Component {

    @Inject
    static ComponentViewRepository repo;

    private Component parent;

    private Component rootComponent;

    public CController() {
    }

    public CController(Object controller) {
        this();
        setController(controller);
    }

    public CController setController(Object controller) {
        ViewComponentBase<Object> view = repo.createView(controller);
        rootComponent = view.getRootComponent();
        return this;
    }

    @Override
    public Iterable<Component> getChildren() {
        return Collections.singletonList(rootComponent);
    }

    @Override
    public Component getParent() {
        return parent;
    }

    @Override
    public void parentChanged(Component newParent) {
        parent = newParent;
    }

    @Override
    public void childRemoved(Component child) {
        throw new RuntimeException("Should not happen");
    }

    @Override
    public void initialize() {

    }

}
