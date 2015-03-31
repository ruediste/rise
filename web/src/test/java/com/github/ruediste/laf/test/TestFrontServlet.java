package com.github.ruediste.laf.test;

import com.github.ruediste.laf.core.entry.ApplicationModule;
import com.github.ruediste.laf.core.entry.FrontServlet;
import com.github.ruediste.salta.jsr330.Salta;

public class TestFrontServlet extends FrontServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void initImpl() throws Exception {
		Salta.createInjector(new ApplicationModule()).injectMembers(this);
	}

}
