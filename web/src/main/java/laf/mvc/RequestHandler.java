package laf.mvc;

import laf.base.ActionResult;
import laf.mvc.actionPath.ActionPath;

public interface RequestHandler<T> {

	ActionResult handle(ActionPath<T> path);
}
