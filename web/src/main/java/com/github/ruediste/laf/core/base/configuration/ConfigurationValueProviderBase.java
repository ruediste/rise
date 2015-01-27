package com.github.ruediste.laf.core.base.configuration;

public abstract class ConfigurationValueProviderBase implements
		ConfigurationValueProvider {

	private ConfigurationValueProvider successor;

	@Override
	public void setSuccessor(ConfigurationValueProvider successor) {
		this.successor = successor;
	}

	protected ConfigurationValueProvider getSuccessor() {
		return successor;
	}

}
