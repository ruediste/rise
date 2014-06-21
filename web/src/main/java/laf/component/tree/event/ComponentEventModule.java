package laf.component.tree.event;

import laf.base.BaseModule;
import laf.component.tree.ComponentTreeModule;

import org.jabsaw.Module;

@Module(description = "Events propagated along the component tree", exported = { ComponentTreeModule.class }, imported = { BaseModule.class })
public class ComponentEventModule {

}
