package laf.component.core;

import laf.core.base.ActionResult;

public interface RequestHandler<T> {

	ActionResult handle(T request);
}
