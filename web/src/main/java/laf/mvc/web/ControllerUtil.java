package laf.mvc.web;

import laf.base.ActionResult;

public interface ControllerUtil {

	ActionResult view(Class<?> viewClass, Object data);

	ActionResult redirect(ActionResult path);
}
