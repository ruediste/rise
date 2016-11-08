package com.github.ruediste.rise.component.components;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.component.render.ComponentState;
import com.github.ruediste.rise.component.tree.CHide;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

/**
 * An expandable panel
 *
 */
public class CPanel extends Component<CPanel> {

    @ComponentState
    public boolean expanded;
    private Runnable headingCollapsed;
    private Runnable body;
    private Runnable headingExpanded;

    private boolean isTransient;

    static class Template extends BootstrapComponentTemplateBase<CPanel> {

        @Override
        public void doRender(CPanel component, BootstrapRiseCanvas<?> html) {
            html.add(new CHide(() -> {
                html.bPanel().bPanelHeading()
                        .add(new CButton(() -> Glyphicon.plus.renderOn(html))
                                .setHandler(() -> component.expanded = true))
                        .render(component.headingCollapsed)._bPanelHeading()._bPanel();
            }).transient_(component.isTransient()).hidden(component.expanded));

            html.add(new CHide(() -> {
                html.bPanel().bPanelHeading()
                        .add(new CButton(() -> Glyphicon.minus.renderOn(html))
                                .setHandler(() -> component.expanded = false))
                        .render(component.headingExpanded)._bPanelHeading();
                html.bPanelBody().render(component.body)._bPanelBody()._bPanel();
            }).transient_(component.isTransient()).hidden(!component.expanded));

        }
    }

    public CPanel(Runnable heading, Runnable body) {
        this(heading, heading, body);
    }

    public CPanel(Runnable headingCollapsed, Runnable headingExpanded, Runnable body) {
        this.headingCollapsed = headingCollapsed;
        this.headingExpanded = headingExpanded;
        this.body = body;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public CPanel transient_(boolean isTransient) {
        this.isTransient = isTransient;
        return this;
    }

}
