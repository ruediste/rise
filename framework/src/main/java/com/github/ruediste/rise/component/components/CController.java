package com.github.ruediste.rise.component.components;

import java.util.Collections;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentViewRepository;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;

/**
 * Embeds the view of a controller
 */
@DefaultTemplate(RenderChildrenTemplate.class)
public class CController extends ComponentBase<CController> {

    private Component rootComponent;

    public CController() {
    }

    public CController(Object controller) {
        this();
        setController(controller);
    }

    public CController setController(Object controller) {
        if (rootComponent != null)
            rootComponent.parentChanged(null);
        if (controller == null)
            rootComponent = null;
        else {
            ViewComponentBase<Object> view = InjectorsHolder.getRestartableInjector()
                    .getInstance(ComponentViewRepository.class).createView(controller);
            rootComponent = view.getRootComponent();
            rootComponent.parentChanged(this);
        }
        return this;
    }

    @Override
    public Iterable<Component> getChildren() {
        if (rootComponent == null)
            return Collections.emptyList();
        else
            return Collections.singletonList(rootComponent);
    }

    @Override
    public void childRemoved(Component child) {
        throw new RuntimeException("Should not happen");
    }

}
