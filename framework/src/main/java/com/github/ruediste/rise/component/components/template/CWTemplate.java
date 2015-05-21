package com.github.ruediste.rise.component.components.template;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.tree.Component;

/**
 * Renders a {@link Component} to HTML and processes updates from the view.
 *
 * <p>
 * {@link Component}s are view technology agnostic. The templates are used to
 * render a component and to parse results sent by the client. A template is
 * associated with each component, but a single template can well be shared
 * between components. The interface was designed to allow this.
 * </p>
 */
public interface CWTemplate<T extends Component> {

	void doRender(T component, HtmlCanvas html) throws IOException;

	void applyValues(T componentl);

	void raiseEvents(T component);
}
