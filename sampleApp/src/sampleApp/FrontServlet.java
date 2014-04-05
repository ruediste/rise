package sampleApp;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;

import laf.DefaultLafConfigurator;
import laf.FrontServletBase;
import laf.LAF;

@WebServlet("/front/*")
public class FrontServlet extends FrontServletBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	DefaultLafConfigurator defaultLafConfigurator;

	@Inject
	LAF laf;
	
	@PostConstruct
	public void initialize() {
		defaultLafConfigurator.configure();
		laf.initialize();
	}
}
