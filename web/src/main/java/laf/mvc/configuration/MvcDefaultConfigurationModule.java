package laf.mvc.configuration;

import laf.base.BaseModule;
import laf.core.http.requestProcessing.HttpRequestProcessingModule;
import laf.core.requestProcessing.RequestProcessingModule;
import laf.mvc.html.MvcHtmlModule;

import org.jabsaw.Module;

@Module(description = "Default configuration for the MVC framework", imported = {
		MvcHtmlModule.class, RequestProcessingModule.class, BaseModule.class,
		HttpRequestProcessingModule.class }, hideFromDependencyGraphOutput = true)
public class MvcDefaultConfigurationModule {

}
