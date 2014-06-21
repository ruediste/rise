package laf.component.basic;

import laf.component.core.ComponentCoreModule;
import laf.component.tree.ComponentTreeModule;

import org.jabsaw.Module;

@Module(description = "Contains basic Components", exported = {
		ComponentCoreModule.class, ComponentTreeModule.class })
public class BasicComponentsModule {

}
