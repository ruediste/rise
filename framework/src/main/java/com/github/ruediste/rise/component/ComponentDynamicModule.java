package com.github.ruediste.rise.component;

import com.github.ruediste.rise.component.core.binding.BindingGroupCreationRule;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.standard.ScopeImpl;
import com.github.ruediste.salta.standard.util.SimpleProxyScopeHandler;

public class ComponentDynamicModule extends AbstractModule {

	@SuppressWarnings("unused")
	private Injector permanentInjector;

	public ComponentDynamicModule(Injector permanentInjector) {
		this.permanentInjector = permanentInjector;

	}

	@Override
	protected void configure() throws Exception {
		bindCreationRule(new BindingGroupCreationRule());

		SimpleProxyScopeHandler scopeHandler = new SimpleProxyScopeHandler(
				"PageScoped");
		bindScope(PageScoped.class, new ScopeImpl(scopeHandler));
		bind(SimpleProxyScopeHandler.class).named("pageScoped").toInstance(
				scopeHandler);

		InitializerUtil.register(config(), ComponentDynamicInitializer.class);

	}
}
