package com.github.ruediste.laf.integration;

import org.jabsaw.Module;

import com.github.ruediste.laf.component.web.defaultConfiguration.ComponentWebDefaultConfigurationModule;
import com.github.ruediste.laf.mvc.web.defaultConfiguration.MvcWebDefaultConfigurationModule;

@Module(description = "Module for the integration of the different Components", imported = {
		MvcWebDefaultConfigurationModule.class,
		ComponentWebDefaultConfigurationModule.class })
public class IntegrationModule {

}
