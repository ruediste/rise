package laf.component.web;

import org.jabsaw.Module;

@Module(imported = {
		laf.core.base.BaseModuleImpl.class,
		laf.core.base.configuration.CoreBaseConfigurationModule.class,
		laf.core.defaultConfiguration.CoreDefaultConfigurationModule.class,
		laf.component.web.requestProcessing.ComponentWebRequestProcessingModule.class,
		laf.component.web.api.ComponentWebApiModule.class,
		laf.component.core.ComponentCoreModule.class,
		laf.component.core.api.ComponentCoreApiModule.class,
		laf.component.core.tree.ComponentTreeModule.class,
		laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		laf.core.http.request.CoreHttpRequestModule.class,
		laf.core.base.attachedProperties.CoreAttachedPropertiesModule.class,
		laf.core.http.CoreHttpModule.class })
public class ComponentWebModule {

}
