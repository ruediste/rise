package laf.component.web.basic.htmlTemplate;

import java.io.IOException;

import laf.component.core.tree.ComponentBase;
import laf.component.web.api.CWRenderUtil;

import org.rendersnake.HtmlCanvas;

public abstract class CRender extends ComponentBase<CRender> {

	abstract public void render(HtmlCanvas html, CWRenderUtil util)
			throws IOException;
}
