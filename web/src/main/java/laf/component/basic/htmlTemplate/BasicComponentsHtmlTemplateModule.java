package laf.component.basic.htmlTemplate;

import laf.component.basic.BasicComponentsModule;
import laf.component.html.ComponentHtmlModule;
import laf.component.html.template.HtmlTemplateModule;

import org.jabsaw.Module;

@Module(description = "Contains HTML templates for the basic components", imported = {
		BasicComponentsModule.class, ComponentHtmlModule.class,
		HtmlTemplateModule.class })
public class BasicComponentsHtmlTemplateModule {

}
