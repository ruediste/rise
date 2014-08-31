package laf.component.web.api;

import org.jabsaw.Module;

@Module(imported = { laf.component.core.tree.ComponentTreeModule.class,
		laf.component.core.pageScope.PageScopeModule.class,
		laf.component.web.template.ComponentWebTemplateModule.class,
		laf.component.web.ComponentWebModule.class,
		laf.component.core.ComponentCoreModule.class })
public class ComponentWebApiModule {

}
