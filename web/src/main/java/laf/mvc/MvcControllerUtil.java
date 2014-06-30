package laf.mvc;

import java.io.IOException;

import javax.inject.Inject;

import laf.base.ActionResult;
import laf.base.configuration.ConfigurationValue;

/**
 * Utility classes for controllers for the MVC framework
 */
public class MvcControllerUtil {

	@Inject
	ConfigurationValue<ViewRenderers> renderers;

	public <TView extends View<TData>, TData> ActionResult view(
			Class<TView> viewClass, TData data) {
		for (ViewRenderer renderer : renderers.value().get()) {
			try {
				ActionResult result = renderer.renderView(viewClass, data);
				if (result != null) {
					return result;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("No ViewRenderer found for view of class "
				+ viewClass);
	}
}
