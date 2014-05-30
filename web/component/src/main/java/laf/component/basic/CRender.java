package laf.component.basic;

import java.io.IOException;

import laf.component.core.ComponentBase;
import laf.component.html.RenderUtil;

import org.rendersnake.HtmlCanvas;

public abstract class CRender extends ComponentBase<CRender> {

	@Override
	abstract public void render(HtmlCanvas html, RenderUtil util)
			throws IOException;
}
