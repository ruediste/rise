package com.github.ruediste.laf.component.web;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.web.annotation.WebAnnotationModule;

@Module(exported = { com.github.ruediste.laf.component.core.ComponentCoreModule.class,
		WebAnnotationModule.class })
public class ComponentWebModule {

}
