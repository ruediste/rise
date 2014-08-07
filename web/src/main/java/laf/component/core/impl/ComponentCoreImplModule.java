package laf.component.core.impl;

import laf.base.BaseModule;
import laf.component.core.ComponentCoreModule;
import laf.component.reqestProcessing.ComponentRequestProcessingModule;
import laf.core.persistence.PersistenceModule;

import org.jabsaw.Module;

@Module(description = "Implementation of the component core moduel", exported = { ComponentCoreModule.class }, imported = {
		BaseModule.class, PersistenceModule.class,
		ComponentRequestProcessingModule.class })
public class ComponentCoreImplModule {

}
