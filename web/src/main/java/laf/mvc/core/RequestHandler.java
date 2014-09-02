package laf.mvc.core;

import laf.core.base.ActionResult;

public interface RequestHandler<T> {

	ActionResult handle(ActionPath<T> path);
}
