package laf.mvc.core.actionPath;

import org.jabsaw.Module;

@Module(exported = {}, imported = { laf.mvc.core.api.MvcCoreApiModule.class,
		laf.core.base.BaseModuleImpl.class,
		laf.core.base.attachedProperties.CoreAttachedPropertiesModule.class,
		laf.mvc.core.MvcCoreModule.class, laf.core.CoreModule.class })
public class MvcActionPathModule {

}
