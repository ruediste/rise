package laf.component.html;

import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.base.ActionResult;
import laf.component.core.Component;

public interface RenderUtil {

	RenderUtil forChild(Component child);

	<T> T path(Class<T> controller);

	String url(ActionResult path);

	ActionPathBuilder path();

	long pageId();

	String getKey(String key);

}
