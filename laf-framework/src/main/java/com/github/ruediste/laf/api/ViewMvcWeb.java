package com.github.ruediste.laf.api;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

/**
 * Base Class for views of the MVC framework
 */
public abstract class ViewMvcWeb<TData> {

	private TData data;

	public final void initialize(TData data) {
		this.data = data;
	}

	public TData getData() {
		return data;
	}

	abstract public void render(HtmlCanvas html) throws IOException;

}