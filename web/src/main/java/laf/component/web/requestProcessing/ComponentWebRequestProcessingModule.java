package laf.component.web.requestProcessing;

import org.jabsaw.Module;

@Module(imported = {
		laf.component.core.tree.ComponentTreeModule.class,
		laf.core.requestParserChain.CoreRequestParserChainModule.class,
		laf.component.web.ComponentWebModule.class,
		laf.component.web.api.ComponentWebApiModule.class,
		laf.core.base.BaseModuleImpl.class,
		laf.component.core.api.ComponentCoreApiModule.class,
		laf.core.CoreModule.class,
		laf.component.core.pageScope.PageScopeModule.class,
		laf.component.core.reqestProcessing.ComponentCoreRequestProcessingModule.class,
		laf.component.web.template.ComponentWebTemplateModule.class,
		laf.core.http.request.CoreHttpRequestModule.class,
		laf.mvc.core.api.MvcCoreApiModule.class,
		laf.core.http.CoreHttpModule.class,
		laf.component.core.ComponentCoreModule.class })
public class ComponentWebRequestProcessingModule {

}
