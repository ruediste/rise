package com.github.ruediste.laf.sample;

import com.github.ruediste.laf.api.DynamicApplicationModule;
import com.github.ruediste.laf.core.front.DynamicApplicationBase;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class App extends DynamicApplicationBase {

	@Override
	protected void startImpl(Injector permanentInjector) {
		Salta.createInjector(new DynamicApplicationModule(permanentInjector))
				.injectMembers(this);
	}

}
