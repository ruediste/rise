package laf.component.html;

import java.io.IOException;

import laf.actionPath.ActionPathFactory.ActionPathBuilder;
import laf.actionPath.PathActionResult;
import laf.base.ActionResult;
import laf.component.tree.Component;

import org.rendersnake.HtmlCanvas;

public interface RenderUtil {

	<T> T path(Class<T> controller);

	String url(ActionResult path);

	ActionPathBuilder path();

	long pageId();

	String getKey(String key);

	void render(HtmlCanvas html, Component component) throws IOException;

	String resourceUrl(String string);

	long getComponentId();

	PathActionResult getReloadPath();
}
