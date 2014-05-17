package laf.component;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

public abstract class CRender extends ComponentBase<CRender> {

	@Override
	abstract public void render(HtmlCanvas html) throws IOException;
}
