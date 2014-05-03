package laf.initialization.laf;

import javax.inject.Singleton;

import laf.initialization.LafInitializer;

/**
 * Initializer depending on all initializers required for the default
 * initialization.
 *
 * <p>
 * Throughout the framework modules, initializers which declare themselves as to
 * be required {@link LafInitializer#before() before} this are defined. This has
 * the effect that an initializer can simply depend on this initializer to make
 * sure the framework is initialized to the default values.
 * </p>
 */
@Singleton
public class DefaultInitializer {

	@LafInitializer(phase = LafConfigurationPhase.class)
	public void configurationInitializer() {

	}

	@LafInitializer(phase = LafInitializationPhase.class)
	public void frameworkInitializer() {

	}
}
