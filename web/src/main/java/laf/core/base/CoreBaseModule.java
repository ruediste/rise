package laf.core.base;

import javax.inject.Singleton;

import laf.core.base.attachedProperties.CoreAttachedPropertiesModule;
import laf.core.base.configuration.CoreBaseConfigurationModule;

import org.jabsaw.Module;

@Module(description = "Meta Module of the Base classes of the LAF Framework", exported = {
		CoreAttachedPropertiesModule.class, CoreBaseConfigurationModule.class,
		BaseModuleImpl.class }, hideFromDependencyGraphOutput = true, includePackage = false)
@Singleton
public class CoreBaseModule {

}
