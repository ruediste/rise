package laf;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.configuration.ConfigurationModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.initialization.InitializationModule;

import org.jabsaw.Module;

@Module(description = "Entry point to the LAF framework", imported = {
		BaseModule.class, ActionPathModule.class,
		HttpRequestMappingModule.class, InitializationModule.class,
		ConfigurationModule.class })
public class LafModule {

}
