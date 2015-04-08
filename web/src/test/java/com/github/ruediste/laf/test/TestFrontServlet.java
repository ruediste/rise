package com.github.ruediste.laf.test;

import com.github.ruediste.laf.core.entry.*;
import com.github.ruediste.salta.jsr330.Salta;

public class TestFrontServlet extends FrontServletBase {

	private static final long serialVersionUID = 1L;

	@Override
	protected void initImpl() throws Exception {
		Salta.createInjector(new ApplicationModule(), new LoggerModule())
				.injectMembers(this);
	}

}
