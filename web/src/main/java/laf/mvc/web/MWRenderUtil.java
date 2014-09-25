package laf.mvc.web;

import laf.core.base.ActionResult;
import laf.core.web.resource.ResourceOutput;

import org.rendersnake.Renderable;

public interface MWRenderUtil {

	public abstract <T> T path(Class<T> controller);

	public abstract String url(ActionResult path);

	public abstract ActionPathBuilder path();

	String url(String path);

	Renderable jsBundle(ResourceOutput output);

	Renderable cssBundle(ResourceOutput output);

}