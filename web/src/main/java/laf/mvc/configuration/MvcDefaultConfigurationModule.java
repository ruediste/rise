package laf.mvc.configuration;

import laf.base.BaseModule;
import laf.mvc.html.MvcHtmlModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Default configuration for the MVC framework", imported = {
		MvcHtmlModule.class, RequestProcessingModule.class, BaseModule.class }, hideFromDependencyGraphOutput = true)
public class MvcDefaultConfigurationModule {

}
