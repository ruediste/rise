package laf.core.defaultConfiguration;

import org.jabsaw.Module;

@Module(imported = {
		laf.core.requestParserChain.CoreRequestParserChainModule.class,
		laf.core.base.configuration.CoreBaseConfigurationModule.class,
		laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		laf.core.http.request.CoreHttpRequestModule.class,
		laf.core.CoreModule.class })
public class CoreDefaultConfigurationModule {

}
