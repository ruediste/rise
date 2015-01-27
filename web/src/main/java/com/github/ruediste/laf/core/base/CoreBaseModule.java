package com.github.ruediste.laf.core.base;

import javax.inject.Singleton;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.base.attachedProperties.CoreAttachedPropertiesModule;
import com.github.ruediste.laf.core.base.configuration.CoreBaseConfigurationModule;

@Module(description = "Meta Module of the Base classes of the LAF Framework", exported = {
		CoreAttachedPropertiesModule.class, CoreBaseConfigurationModule.class,
		BaseModuleImpl.class }, hideFromDependencyGraphOutput = true, includePackage = false)
@Singleton
public class CoreBaseModule {

}
