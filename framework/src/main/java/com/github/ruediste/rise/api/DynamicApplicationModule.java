package com.github.ruediste.rise.api;

import com.github.ruediste.rise.component.ComponentDynamicModule;
import com.github.ruediste.rise.core.CoreDynamicModule;
import com.github.ruediste.rise.core.front.LoggerModule;
import com.github.ruediste.rise.mvc.web.MvcWebDynamicModule;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;

public class DynamicApplicationModule extends AbstractModule {

	private Injector permanentInjector;

	public DynamicApplicationModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;

	}

	@Override
	protected void configure() throws Exception {
		installMvcWebModule();
		installComponentModule();
		installCoreModule();
		installLoggerModule();

	}

	protected void installComponentModule() {
		install(new ComponentDynamicModule(permanentInjector));
	}

	protected void installMvcWebModule() {
		install(new MvcWebDynamicModule(permanentInjector));
	}

	protected void installCoreModule() {
		install(new CoreDynamicModule(permanentInjector));
	}

	protected void installLoggerModule() {
		install(new LoggerModule());
	}

}
