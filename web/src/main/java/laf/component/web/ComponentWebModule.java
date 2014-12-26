package laf.component.web;

import laf.core.web.annotation.WebAnnotationModule;

import org.jabsaw.Module;

@Module(exported = { laf.component.core.ComponentCoreModule.class,
		WebAnnotationModule.class })
public class ComponentWebModule {

}
