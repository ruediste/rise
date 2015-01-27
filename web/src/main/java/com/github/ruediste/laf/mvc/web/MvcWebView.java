package com.github.ruediste.laf.mvc.web;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.mvc.core.MvcView;

/**
 * Base Class for views of the MVC framework
 */
public abstract class MvcWebView<TData> extends MvcView<TData> {

	abstract public void render(HtmlCanvas html, MWRenderUtil util)
			throws IOException;
}