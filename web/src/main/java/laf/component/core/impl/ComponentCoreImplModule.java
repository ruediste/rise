package laf.component.core.impl;

import laf.base.BaseModule;
import laf.component.core.ComponentCoreModule;
import laf.component.pageScope.PageScopeModule;
import laf.persistence.PersistenceModule;

import org.jabsaw.Module;

@Module(description = "Implementation of the component core moduel", exported = { ComponentCoreModule.class }, imported = {
		BaseModule.class, PageScopeModule.class, PersistenceModule.class })
public class ComponentCoreImplModule {

}
