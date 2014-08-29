package laf.mvc.core;

import laf.core.base.BaseModule;
import laf.core.persistence.PersistenceModule;

import org.jabsaw.Module;

@Module(description = "Base module of the MVC framework", imported = {
		BaseModule.class, PersistenceModule.class })
public class MvcModule {

}
