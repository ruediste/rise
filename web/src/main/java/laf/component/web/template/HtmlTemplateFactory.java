package laf.component.web.template;

import laf.component.core.tree.Component;
import laf.component.web.api.CWTemplate;

/**
 * Used to create {@link CWTemplate}s from {@link Component}s
 */
public interface HtmlTemplateFactory {

	/**
	 * Create a {@link CWTemplate} for the given component. This method is called
	 * at most once per component. The returned template is associated with the
	 * component.
	 */
	<T extends Component> CWTemplate<T> createTemplate(T component);
}
