package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Renders a {@link Component} to HTML and processes updates from the view.
 *
 * <p>
 * {@link Component}s are view technology agnostic. The templates are used to
 * render a component and to parse results sent by the client. A template is
 * associated with each component, but a single template can well be shared
 * between components. The interface was designed to allow this.
 * </p>
 * 
 * <p>
 * Instances are held by the {@link ComponentTemplateIndex} and instantiated
 * only once.
 * </p>
 */
public interface ComponentTemplate<T extends Component> {

    void doRender(T component, RiseCanvas<?> html);

    void applyValues(T componentl);

    void raiseEvents(T component);

    void handleAjaxRequest(T component) throws Throwable;
}
