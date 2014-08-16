package laf.core.html;

import java.io.IOException;

import laf.base.ActionResult;
import laf.mvc.actionPath.ActionPathFactory.ActionPathBuilder;

import org.rendersnake.HtmlCanvas;

public interface RenderUtilBase {

	public abstract <T> T path(Class<T> controller);

	public abstract String url(ActionResult path);

	public abstract ActionPathBuilder path();

	public abstract String resourceUrl(String string);

	/**
	 * Write the start of a html page (doctype plus html element) to the given
	 * canvas
	 */
	public abstract void startHtmlPage(HtmlCanvas html) throws IOException;

}