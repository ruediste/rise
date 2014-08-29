package laf.component.core.tree.event;

import laf.component.core.tree.ComponentTreeModule;
import laf.core.base.BaseModule;

import org.jabsaw.Module;

@Module(description = "Events propagated along the component tree", exported = { ComponentTreeModule.class }, imported = { BaseModule.class })
public class ComponentEventModule {

}
