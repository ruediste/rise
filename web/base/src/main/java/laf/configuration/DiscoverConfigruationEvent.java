package laf.configuration;

public interface DiscoverConfigruationEvent {
	/**
	 * Add the given definer to the configuration. Definers added later take
	 * precedence
	 */
	void add(ConfigurationDefiner definer);

	/**
	 * Add the given properties file to the configuration. The file is loaded
	 * from the classpath. Definers added later take precedence
	 */
	void addPropretiesFile(String path);

	/**
	 * Add the given definer to this configuration. Definers added later take
	 * precedence
	 */
	void add(ConfigurationValueProvider provider);
}
