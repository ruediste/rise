package laf.component.html.impl;

import laf.base.BaseModule;
import laf.component.core.impl.ComponentCoreImplModule;
import laf.component.html.template.HtmlTemplateModule;
import laf.component.reqestProcessing.ComponentRequestProcessingModule;
import laf.core.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Implementation of HTML related functionality of the component framework", imported = {
		BaseModule.class, HtmlTemplateModule.class,
		RequestProcessingModule.class, ComponentCoreImplModule.class,
		ComponentRequestProcessingModule.class })
public class ComponentHtmlImplModule {

}
