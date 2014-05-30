package laf.component.html.template;

import laf.component.core.Component;

/**
 * Used to create {@link HtmlTemplate}s from {@link Component}s
 */
public interface HtmlTemplateFactory {

	/**
	 * Create a {@link HtmlTemplate} for the given component. This method is called
	 * at most once per component. The returned template is associated with the
	 * component.
	 */
	<T extends Component> HtmlTemplate<T> createTemplate(T component);
}
