package laf.component.web.api;

import java.io.IOException;

import laf.component.core.tree.Component;
import laf.component.web.ApplyValuesUtil;

import org.rendersnake.HtmlCanvas;

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

	void render(T component, HtmlCanvas html, CWRenderUtil util)
			throws IOException;

	void applyValues(T component, ApplyValuesUtil util);

	void raiseEvents(T component, CWRaiseEventsUtil util);
}
