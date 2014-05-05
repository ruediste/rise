package laf.initialization.laf;

import javax.inject.Singleton;

import laf.initialization.LafInitializer;

@Singleton
public class FrameworkRootInitializer {

	private boolean initialized;

	@LafInitializer(phase = LafConfigurationPhase.class)
	public void initializeConfiguration() {
		initialized = true;
	}

	@LafInitializer(phase = LafInitializationPhase.class)
	public void initializeInitialization() {
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}
}
