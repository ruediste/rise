package laf.component.web.template;

import org.jabsaw.Module;

@Module(imported = {
		laf.core.base.configuration.CoreBaseConfigurationModule.class,
		laf.component.core.tree.ComponentTreeModule.class,
		laf.core.base.attachedProperties.CoreAttachedPropertiesModule.class,
		laf.core.http.request.CoreHttpRequestModule.class,
		laf.component.web.ComponentWebModule.class,
		laf.component.web.api.ComponentWebApiModule.class,
		laf.core.http.CoreHttpModule.class })
public class ComponentWebTemplateModule {

}
