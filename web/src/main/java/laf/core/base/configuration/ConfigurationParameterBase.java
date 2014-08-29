package laf.core.base.configuration;

public abstract class ConfigurationParameterBase implements
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
