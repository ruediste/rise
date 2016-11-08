package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ViewFactory;
import com.github.ruediste.rise.component.render.RiseCanvasTarget;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;

/**
 * Embeds a view or the view of a controller
 */
public class CView extends Component<CView> {

    private ViewComponentBase<?> displayedView;

    public static class Template extends BootstrapComponentTemplateBase<CView> {

        @Override
        public void doRender(CView component, BootstrapRiseCanvas<?> html) {
            RiseCanvasTarget target = html.internal_target();
            ViewComponentBase<?> oldView = target.getView();
            try {
                target.setView(component.getDisplayedView());
                ViewComponentBase<?> view = component.getDisplayedView();
                view.parentComponent = component;
                view.render(html);
            } finally {
                target.setView(oldView);
            }
        }

    }

    public CView() {
    }

    public CView(SubControllerComponent ctrl) {
        setController(ctrl);
    }

    public CView(ViewComponentBase<?> displayedView) {
        this.displayedView = displayedView;
    }

    public CView setController(SubControllerComponent controller) {
        if (controller == null)
            setDisplayedView(null);
        else {
            setDisplayedView(InjectorsHolder.getRestartableInjector().getInstance(ViewFactory.class)
                    .createView(controller, false, null));
        }
        return this;
    }

    public ViewComponentBase<?> getDisplayedView() {
        return displayedView;
    }

    public CView setDisplayedView(ViewComponentBase<?> displayedView) {
        this.displayedView = displayedView;
        return this;
    }

}
