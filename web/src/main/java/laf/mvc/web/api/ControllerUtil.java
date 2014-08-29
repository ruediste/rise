package laf.mvc.web.api;

import laf.core.base.ActionResult;

public interface ControllerUtil {

	ActionResult view(Class<?> viewClass, Object data);

	ActionResult redirect(ActionResult path);
}
