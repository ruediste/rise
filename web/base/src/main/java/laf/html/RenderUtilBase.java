package laf.html;

import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.base.ActionResult;

public interface RenderUtilBase {

	public abstract <T> T path(Class<T> controller);

	public abstract String url(ActionResult path);

	public abstract ActionPathBuilder path();

	public abstract String resourceUrl(String string);

}