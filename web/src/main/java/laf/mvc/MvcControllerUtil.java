package laf.mvc;

/**
 * Utility classes for controllers for the MVC framework
 */
public class MvcControllerUtil {

	public <TView extends View<TData>, TData> ViewActionResult<TView, TData> view(
			Class<TView> viewClass, TData data) {
		return new ViewActionResult<>(viewClass, data);
	}
}
