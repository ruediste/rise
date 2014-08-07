package laf.component.defaultConfiguration;

import laf.base.BaseModule;
import laf.component.ComponentModule;
import laf.component.basic.htmlTemplate.BasicComponentsHtmlTemplateModule;
import laf.component.html.impl.ComponentHtmlImplModule;
import laf.component.html.template.HtmlTemplateModule;
import laf.component.reqestProcessing.ComponentRequestProcessingModule;
import laf.core.http.requestProcessing.HttpRequestProcessingModule;
import laf.core.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Default configuration of the component framework", imported = {
		BaseModule.class, HtmlTemplateModule.class,
		ComponentRequestProcessingModule.class, ComponentHtmlImplModule.class,
		HtmlTemplateModule.class, BasicComponentsHtmlTemplateModule.class,
		RequestProcessingModule.class, ComponentModule.class,
		HttpRequestProcessingModule.class }, hideFromDependencyGraphOutput = true)
public class ComponentDefaultConfigurationModule {

}
