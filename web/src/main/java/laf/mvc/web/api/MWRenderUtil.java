package laf.mvc.web.api;

import laf.core.base.ActionResult;
import laf.mvc.web.ActionPathBuilder;

public interface MWRenderUtil {

	public abstract <T> T path(Class<T> controller);

	public abstract String url(ActionResult path);

	public abstract ActionPathBuilder path();

}