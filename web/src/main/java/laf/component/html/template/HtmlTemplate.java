package laf.component.html.template;

import java.io.IOException;

import laf.component.html.ApplyValuesUtil;
import laf.component.html.RenderUtil;
import laf.component.tree.Component;

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
public interface HtmlTemplate<T extends Component> {

	void render(T component, HtmlCanvas html, RenderUtil util)
			throws IOException;

	void applyValues(T component, ApplyValuesUtil util);

	void raiseEvents(T component, RaiseEventsUtil util);
}
