package com.github.ruediste.laf.mvc.web;

import org.rendersnake.Renderable;

import com.github.ruediste.laf.core.base.ActionResult;
import com.github.ruediste.laf.core.web.resource.ResourceOutput;

public interface MWRenderUtil {

	public abstract <T> T path(Class<T> controller);

	public abstract String url(ActionResult path);

	public abstract ActionPathBuilder path();

	String url(String path);

	Renderable jsBundle(ResourceOutput output);

	Renderable cssBundle(ResourceOutput output);

}