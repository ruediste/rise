package laf.component.html;

import java.io.IOException;

import laf.actionPath.PathActionResult;
import laf.component.tree.Component;
import laf.html.RenderUtilBase;

import org.rendersnake.HtmlCanvas;

public interface RenderUtil extends RenderUtilBase {

	long pageId();

	String getKey(String key);

	void render(HtmlCanvas html, Component component) throws IOException;

	long getComponentId();

	PathActionResult getReloadPath();
}