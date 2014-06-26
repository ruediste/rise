package laf.mvc.html;

import java.io.IOException;

import laf.mvc.View;

import org.rendersnake.HtmlCanvas;

/**
 * Base Class for views of the MVC framework
 */
public abstract class RendersnakeView<TData> extends View<TData> {

	abstract public void render(HtmlCanvas html, MvcRenderUtil util)
			throws IOException;
}