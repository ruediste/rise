package laf.integration;

import laf.component.web.defaultConfiguration.ComponentWebDefaultConfigurationModule;
import laf.mvc.web.defaultConfiguration.MvcWebDefaultConfigurationModule;

import org.jabsaw.Module;

@Module(description = "Module for the integration of the different Components", imported = {
		MvcWebDefaultConfigurationModule.class,
		ComponentWebDefaultConfigurationModule.class })
public class IntegrationModule {

}
