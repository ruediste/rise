package sampleApp;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;

import laf.FrontServletBase;
import laf.Laf;

@WebServlet("/front/*")
public class FrontServlet extends FrontServletBase {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	Laf laf;

	@PostConstruct
	public void initialize() {
		laf.initialize();
	}
}
