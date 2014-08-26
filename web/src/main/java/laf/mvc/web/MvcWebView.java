package laf.mvc.web;

import java.io.IOException;

import laf.mvc.MvcView;

import org.rendersnake.HtmlCanvas;

/**
 * Base Class for views of the MVC framework
 */
public abstract class MvcWebView<TData> extends MvcView<TData> {

	abstract public void render(HtmlCanvas html, RenderUtil util)
			throws IOException;
}