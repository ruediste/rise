package com.github.ruediste.laf.sample;

import javax.servlet.annotation.WebServlet;

import com.github.ruediste.laf.api.PermanentApplicationModule;
import com.github.ruediste.laf.core.front.FrontServletBase;
import com.github.ruediste.salta.jsr330.Salta;

@WebServlet(urlPatterns = "/*")
public class FrontServlet extends FrontServletBase {
	public FrontServlet() {
		super(App.class);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void initImpl() throws Exception {
		Salta.createInjector(new PermanentApplicationModule()).injectMembers(
				this);
	}

}
