package laf.component.html.template;

import laf.base.BaseModule;
import laf.component.core.ComponentCoreModule;
import laf.component.html.ComponentHtmlModule;

import org.jabsaw.Module;

@Module(description = "Templates used to render Components", exported = {
		ComponentCoreModule.class, ComponentHtmlModule.class, BaseModule.class })
public class HtmlTemplateModule {

}
