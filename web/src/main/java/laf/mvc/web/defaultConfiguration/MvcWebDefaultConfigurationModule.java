package laf.mvc.web.defaultConfiguration;

import org.jabsaw.Module;

@Module(imported = {
		laf.core.requestParserChain.CoreRequestParserChainModule.class,
		laf.core.base.configuration.CoreBaseConfigurationModule.class,
		laf.core.defaultConfiguration.CoreDefaultConfigurationModule.class,
		laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		laf.core.http.request.CoreHttpRequestModule.class,
		laf.mvc.core.MvcCoreModule.class, laf.mvc.web.MvcWebModule.class })
public class MvcWebDefaultConfigurationModule {

}
