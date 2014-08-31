package laf.component.core.tree.event;

import laf.component.core.tree.ComponentTreeModule;
import laf.core.base.CoreBaseModule;

import org.jabsaw.Module;

@Module(description = "Events propagated along the component tree", exported = { ComponentTreeModule.class }, imported = { CoreBaseModule.class })
public class ComponentEventModule {

}
