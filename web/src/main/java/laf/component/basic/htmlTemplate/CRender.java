package laf.component.basic.htmlTemplate;

import java.io.IOException;

import laf.component.html.RenderUtil;
import laf.component.tree.ComponentBase;

import org.rendersnake.HtmlCanvas;

public abstract class CRender extends ComponentBase<CRender> {

	abstract public void render(HtmlCanvas html, RenderUtil util)
			throws IOException;
}
