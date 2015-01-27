package com.github.ruediste.laf.mvc.core;

import com.github.ruediste.laf.core.base.ActionResult;

public interface RequestHandler<T> {

	ActionResult handle(ActionPath<T> path);
}
