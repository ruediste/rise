package laf.component.web.defaultConfiguration;

import laf.component.web.components.template.ComponentWebBasicTemplateModule;

import org.jabsaw.Module;

@Module(exported = {
		laf.core.defaultConfiguration.CoreDefaultConfigurationModule.class,
		laf.component.web.ComponentWebModule.class,
		ComponentWebBasicTemplateModule.class })
public class ComponentWebDefaultConfigurationModule {

}
