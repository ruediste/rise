package laf.mvc.web;

import org.jabsaw.Module;

@Module(imported = { laf.mvc.core.MvcCoreModule.class,
		laf.core.requestParserChain.CoreRequestParserChainModule.class,
		laf.mvc.web.api.MvcWebApiModule.class,
		laf.core.http.request.CoreHttpRequestModule.class,
		laf.mvc.core.actionPath.MvcActionPathModule.class,
		laf.mvc.core.api.MvcCoreApiModule.class,
		laf.core.http.CoreHttpModule.class, laf.core.base.BaseModuleImpl.class,
		laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		laf.core.CoreModule.class })
public class MvcWebModule {

}
