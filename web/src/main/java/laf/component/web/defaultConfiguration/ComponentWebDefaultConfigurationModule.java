package laf.component.web.defaultConfiguration;

import org.jabsaw.Module;

@Module(imported = {
		laf.component.core.reqestProcessing.ComponentCoreRequestProcessingModule.class,
		laf.core.defaultConfiguration.CoreDefaultConfigurationModule.class,
		laf.core.requestParserChain.CoreRequestParserChainModule.class,
		laf.core.http.request.CoreHttpRequestModule.class,
		laf.component.web.requestProcessing.ComponentWebRequestProcessingModule.class,
		laf.component.web.ComponentWebModule.class,
		laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		laf.component.core.ComponentCoreModule.class })
public class ComponentWebDefaultConfigurationModule {

}
