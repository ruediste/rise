package laf.mvc.core;

import laf.core.base.CoreBaseModule;
import laf.core.persistence.PersistenceModule;

import org.jabsaw.Module;

@Module(description = "Base module of the MVC framework", imported = {
		CoreBaseModule.class, PersistenceModule.class })
public class MvcModule {

}
