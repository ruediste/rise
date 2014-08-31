package laf.component.core.reqestProcessing;

import org.jabsaw.Module;

@Module(imported = { laf.component.core.ComponentCoreModule.class,
		laf.core.base.BaseModuleImpl.class,
		laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		laf.core.http.request.CoreHttpRequestModule.class,
		laf.core.persistence.PersistenceModule.class,
		laf.component.core.pageScope.PageScopeModule.class })
public class ComponentCoreRequestProcessingModule {

}
