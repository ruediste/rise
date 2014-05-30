package laf.component;

import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.base.ActionResult;

public interface RenderUtil {

	RenderUtil forChild(Component child);

	<T> T path(Class<T> controller);

	String url(ActionResult path);

	ActionPathBuilder path();

	long pageId();

	String getKey(String key);

}
