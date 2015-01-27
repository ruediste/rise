package com.github.ruediste.laf.mvc.web;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.web.annotation.WebAnnotationModule;

@Module(exported = { com.github.ruediste.laf.mvc.core.MvcCoreModule.class,
		WebAnnotationModule.class })
public class MvcWebModule {

}
