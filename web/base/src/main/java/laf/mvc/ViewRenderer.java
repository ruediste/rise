package laf.mvc;

import laf.base.ActionResult;

/**
 * Renderer for {@link View} instances. Used by the {@link ViewActionResult} to
 * render the view upon construction
 */
public interface ViewRenderer {

	/**
	 * Render the given view with the given data. Return the action result
	 * representing the view, or null if the view could not be rendered
	 */
	ActionResult renderView(Class<?> viewClass, Object data);
}
