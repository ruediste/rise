package laf.configuration;

import javax.inject.Singleton;

import laf.initialization.*;

import org.jabsaw.Module;

/**
 * The configuration module is used to configure the framework. Every
 * {@link Singleton} can define {@link ConfigurationParameter}s. A parameter has
 * a default value which can be overwritten during application startup.
 *
 * <p>
 * When the {@link CreateInitializersEvent} is raised, all parameters are
 * scanned for {@link Initializer}s using
 * {@link InitializationService#createInitializers(Object)}. This allows easy
 * adaption of the initialization process to the configuration.
 * </p>
 */
@Module(imported = InitializationModule.class)
public class ConfigurationModule {

}
