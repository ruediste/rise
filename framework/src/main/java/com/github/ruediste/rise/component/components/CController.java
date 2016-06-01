package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;

/**
 * Embeds the view of a controller
 */
@DefaultTemplate(CControllerTemplate.class)
public class CController extends ComponentBase<CController> {

    private ViewComponentBase<?> view;

    public CController() {
    }

    public CController(SubControllerComponent ctrl) {
        setController(ctrl);
    }

    public CController setController(SubControllerComponent controller) {
        if (controller == null)
            view = null;
        else {
            view = InjectorsHolder.getRestartableInjector().getInstance(ComponentConfiguration.class)
                    .createView(controller, false);
        }
        return this;
    }

    public ViewComponentBase<?> getView() {
        return view;
    }

}
