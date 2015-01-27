package com.github.ruediste.laf.mvc.core;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.CoreModule;

@Module(description = "Base module of the MVC framework", exported = {
		CoreModule.class, com.github.ruediste.laf.mvc.core.api.MvcCoreApiModule.class })
public class MvcCoreModule {

}
