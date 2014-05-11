package laf;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.configuration.ConfigurationModule;
import laf.requestProcessing.http.HttpRequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Entry point to the LAF framework", imported = {
		BaseModule.class, ActionPathModule.class,
		HttpRequestProcessingModule.class, ConfigurationModule.class })
public class LafModule {

}
