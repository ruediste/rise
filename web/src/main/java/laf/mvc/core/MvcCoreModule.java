package laf.mvc.core;

import laf.core.CoreModule;

import org.jabsaw.Module;

@Module(description = "Base module of the MVC framework", exported = {
		CoreModule.class, laf.mvc.core.api.MvcCoreApiModule.class })
public class MvcCoreModule {

}
