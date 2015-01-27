package laf.skeleton;

import javax.servlet.annotation.WebServlet;

import com.github.ruediste.laf.core.front.FrontServletBase;

/**
 * Servlet processing all requests handled by the framework.
 */
@WebServlet(value = "/*", loadOnStartup = 10)
public class FrontServlet extends FrontServletBase {
	private static final long serialVersionUID = 1L;
}
