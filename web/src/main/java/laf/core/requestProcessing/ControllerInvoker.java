package laf.core.requestProcessing;

import laf.base.ActionResult;
import laf.core.actionPath.ActionPath;

public interface ControllerInvoker {
	ActionResult invoke(ActionPath<Object> actionPath);
}