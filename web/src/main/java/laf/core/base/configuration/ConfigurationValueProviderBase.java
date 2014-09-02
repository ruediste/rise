package laf.core.base.configuration;

public abstract class ConfigurationValueProviderBase implements
ConfigurationValueProvider {

	private ConfigurationValueProvider successor;

	@Override
	public void setSuccessor(ConfigurationValueProvider successor) {
		this.successor = successor;
	}

	ConfigurationValueProvider getSuccessor() {
		return successor;
	}
}
