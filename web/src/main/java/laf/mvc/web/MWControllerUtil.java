package laf.mvc.web;

import laf.core.base.ActionResult;

public interface MWControllerUtil {

	ActionResult view(Class<?> viewClass, Object data);

	ActionResult redirect(ActionResult path);
}
