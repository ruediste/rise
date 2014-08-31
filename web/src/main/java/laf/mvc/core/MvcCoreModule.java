package laf.mvc.core;

import org.jabsaw.Module;

@Module(description = "Base module of the MVC framework", imported = {
		laf.core.base.BaseModuleImpl.class,
		laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		laf.core.CoreModule.class,
		laf.core.persistence.PersistenceModule.class,
		laf.mvc.core.actionPath.MvcActionPathModule.class })
public class MvcCoreModule {

}
