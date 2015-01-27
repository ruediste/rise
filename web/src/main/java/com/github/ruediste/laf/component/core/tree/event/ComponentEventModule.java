package com.github.ruediste.laf.component.core.tree.event;

import org.jabsaw.Module;

import com.github.ruediste.laf.component.core.tree.ComponentTreeModule;
import com.github.ruediste.laf.core.base.attachedProperties.CoreAttachedPropertiesModule;

@Module(description = "Events propagated along the component tree", exported = { ComponentTreeModule.class }, imported = { CoreAttachedPropertiesModule.class })
public class ComponentEventModule {

}
