package laf.component.html.impl;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.component.core.ComponentCoreModule;
import laf.component.html.ComponentHtmlModule;
import laf.component.html.template.HtmlTemplateModule;
import laf.component.pageScope.PageScopeModule;
import laf.component.tree.ComponentTreeModule;
import laf.http.HttpModule;

import org.jabsaw.Module;

@Module(description = "Implementation of HTML related functionality of the component framework", imported = { BaseModule.class }, exported = {
		ComponentTreeModule.class, ActionPathModule.class, HttpModule.class,
		ComponentCoreModule.class, ComponentHtmlModule.class,
		HtmlTemplateModule.class, PageScopeModule.class })
public class ComponentHtmlImplModule {

}
