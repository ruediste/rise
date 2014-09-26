package laf.component.web.basic;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

public interface Renderer {

	void accept(HtmlCanvas html) throws IOException;
}
