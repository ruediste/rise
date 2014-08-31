package laf.core.http;

import org.jabsaw.Module;

@Module(imported = {
		laf.core.requestParserChain.CoreRequestParserChainModule.class,
		laf.core.base.BaseModuleImpl.class,
		laf.core.base.configuration.CoreBaseConfigurationModule.class,
		laf.core.http.request.CoreHttpRequestModule.class })
public class CoreHttpModule {

}
