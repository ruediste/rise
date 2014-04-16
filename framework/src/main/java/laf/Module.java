package laf;

import laf.configuration.ConfigurationModule;
import laf.initialization.InitializationModule;

/**
 *
 * @author ruedi
 *
 */
public class Module {

	/**
	 * The framework initialization is implemented using the
	 * {@link InitializationModule}. Framework modules and extensions can define
	 * initializers, which can declare dependencies upon each other. Then they
	 * are run in an order respecting these dependencies.
	 *
	 * <p>
	 * The set of initializers which is executed is determined by the
	 * {@link ConfigurationModule}. Every configured parameter can specify
	 * initializers which need to be executed.
	 * </p>
	 */
	public void configurationAndInitialization() {

	}
}
