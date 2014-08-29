package laf.mvc.core;

import laf.core.base.ActionResult;
import laf.mvc.core.actionPath.ActionPath;

public interface RequestHandler<T> {

	ActionResult handle(ActionPath<T> path);
}
