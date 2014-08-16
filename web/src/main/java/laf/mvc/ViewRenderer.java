package laf.mvc;

import java.io.IOException;

import laf.base.ActionResult;
import laf.mvc.html.View;

/**
 * Renderer for {@link View} instances. Used by the {@link ViewActionResult} to
 * render the view upon construction
 */
public interface ViewRenderer {

	/**
	 * Render the given view with the given data. Return the action result
	 * representing the view, or null if the view could not be rendered
	 * 
	 * @throws IOException
	 */
	ActionResult renderView(Class<?> viewClass, Object data) throws IOException;
}
