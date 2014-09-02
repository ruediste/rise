package laf.mvc.web;

import laf.core.base.ActionResult;

public interface MWRenderUtil {

	public abstract <T> T path(Class<T> controller);

	public abstract String url(ActionResult path);

	public abstract ActionPathBuilder path();

}