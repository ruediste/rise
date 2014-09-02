package sampleApp;

import javax.servlet.annotation.WebServlet;

import laf.core.front.FrontServletBase;

@WebServlet(value = "/front/*", loadOnStartup = 10)
public class FrontServlet extends FrontServletBase {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}
