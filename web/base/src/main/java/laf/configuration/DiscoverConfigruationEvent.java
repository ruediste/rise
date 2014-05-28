package laf.configuration;

public interface DiscoverConfigruationEvent {
	void add(ConfigurationDefiner definer);

	void addPropretiesFile(String path);

	void add(ConfigurationValueProvider provider);
}
