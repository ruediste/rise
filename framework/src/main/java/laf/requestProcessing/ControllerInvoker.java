package laf.requestProcessing;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;

public interface ControllerInvoker {
	ActionResult invoke(ActionPath<Object> actionPath);
}