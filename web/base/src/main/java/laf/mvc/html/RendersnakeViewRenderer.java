package laf.mvc.html;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.mvc.ViewActionResult;
import laf.mvc.ViewRenderer;

public class RendersnakeViewRenderer implements ViewRenderer {

	@Inject
	Instance<Object> viewInstance;

	public boolean renderResult(ActionResult result,
			HttpServletResponse response) throws IOException {
		if (result instanceof ViewActionResult<?, ?>) {
			ViewActionResult<?, ?> viewActionResult = (ViewActionResult<?, ?>) result;

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private <TData> void initializeView(RendersnakeView<TData> view, Object data) {
		view.initialize((TData) data);
	}

	@Override
	public ActionResult renderView(Class<?> viewClass, Object data) {
		if (RendersnakeView.class.isAssignableFrom(viewClass)) {
			RendersnakeView<?> view = (RendersnakeView<?>) viewInstance.select(
					viewClass).get();
			initializeView(view, data);
			view.render(null, null);
		}
		return null;
	}
}
