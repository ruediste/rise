package com.github.ruediste.laf.sample.front;

import javax.naming.Context;

import org.jnp.interfaces.NamingContextFactory;

import com.github.ruediste.laf.integration.StandaloneLafApplication;

public class SampleMain {

	public static void main(String[] args) throws Exception {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				NamingContextFactory.class.getName());
		new StandaloneLafApplication().start(FrontServlet.class);
	}
}
