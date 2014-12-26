package laf.mvc.web;

import laf.core.web.annotation.WebAnnotationModule;

import org.jabsaw.Module;

@Module(exported = { laf.mvc.core.MvcCoreModule.class,
		WebAnnotationModule.class })
public class MvcWebModule {

}
