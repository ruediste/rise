package com.github.ruediste.laf.component.core;

import com.github.ruediste.laf.core.base.ActionResult;

public interface RequestHandler<T> {

	ActionResult handle(T request);
}
