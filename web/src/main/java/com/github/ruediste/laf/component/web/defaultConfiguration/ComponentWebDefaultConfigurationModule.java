package com.github.ruediste.laf.component.web.defaultConfiguration;

import org.jabsaw.Module;

import com.github.ruediste.laf.component.web.components.template.ComponentWebBasicTemplateModule;

@Module(exported = {
		com.github.ruediste.laf.core.defaultConfiguration.CoreDefaultConfigurationModule.class,
		com.github.ruediste.laf.component.web.ComponentWebModule.class,
		ComponentWebBasicTemplateModule.class })
public class ComponentWebDefaultConfigurationModule {

}
