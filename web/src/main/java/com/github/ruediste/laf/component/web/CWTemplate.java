package com.github.ruediste.laf.component.web;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.core.tree.Component;

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

	void render(T component, HtmlCanvas html)
			throws IOException;

	void applyValues(T component, ApplyValuesUtil util);

	void raiseEvents(T component, CWRaiseEventsUtil util);
}
